document.addEventListener('DOMContentLoaded', async () => {
    // Fetch backend URL from config
    let backendUrl = 'http://localhost:8081'; // Default fallback
    try {
        const configResponse = await fetch('/config');
        if (configResponse.ok) {
            const config = await configResponse.json();
            backendUrl = config.backendUrl;
            console.log('Using backend URL from config:', backendUrl);
        } else {
            console.warn('Failed to fetch config, using default backend URL');
        }
    } catch (error) {
        console.error('Error fetching config:', error);
        console.warn('Using default backend URL');
    }

    // Utility functions for base64 encoding/decoding
    function base64UrlDecode(base64Url) {
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const padLength = (4 - (base64.length % 4)) % 4;
        const padded = base64 + '='.repeat(padLength);
        return atob(padded);
    }

    function base64UrlToArrayBuffer(base64Url) {
        const binary = base64UrlDecode(base64Url);
        const bytes = new Uint8Array(binary.length);
        for (let i = 0; i < binary.length; i++) {
            bytes[i] = binary.charCodeAt(i);
        }
        return bytes.buffer;
    }

    function arrayBufferToBase64Url(buffer) {
        const bytes = new Uint8Array(buffer);
        let binary = '';
        for (let i = 0; i < bytes.byteLength; i++) {
            binary += String.fromCharCode(bytes[i]);
        }
        const base64 = btoa(binary);
        return base64.replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '');
    }

    // Prepare publicKey options for WebAuthn
    function preparePublicKeyOptions(options) {
        return {
            ...options,
            challenge: base64UrlToArrayBuffer(options.challenge),
            user: {
                ...options.user,
                id: base64UrlToArrayBuffer(options.user.id)
            },
            excludeCredentials: options.excludeCredentials ?
                options.excludeCredentials.map(cred => ({
                    ...cred,
                    id: base64UrlToArrayBuffer(cred.id)
                })) : []
        };
    }

    // Format credential for server
    function formatCredentialForServer(credential) {
        return {
            id: credential.id,
            displayName: "WebAuthn Credential",
            createdDateTime: new Date().toISOString(),
            aaGuid: "",
            model: navigator.userAgent,
            attestationCertificates: [],
            attestationLevel: "none",
            publicKeyCredential: {
                id: credential.id,
                response: {
                    clientDataJSON: arrayBufferToBase64Url(credential.response.clientDataJSON),
                    attestationObject: arrayBufferToBase64Url(credential.response.attestationObject)
                },
                clientExtensionResults: credential.getClientExtensionResults()
            }
        };
    }

    // Complete registration with WebAuthn credential
    async function completeRegistration(initiationResponse) {
        try {
            // Prepare publicKey options for WebAuthn
            const publicKeyOptions = preparePublicKeyOptions(initiationResponse.publicKey);

            // Create credential
            const credential = await navigator.credentials.create({
                publicKey: publicKeyOptions
            });

            // Format credential for server
            const formattedCredential = formatCredentialForServer(credential);

            // Send to server
            const completeResponse = await fetch(`${backendUrl}/registration/complete/${initiationResponse.creationRequestId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formattedCredential)
            });

            const completeResult = await completeResponse.json();

            return {
                success: completeResponse.ok,
                message: completeResponse.ok ?
                    'Registration completed successfully!' :
                    (completeResult.message || 'Failed to complete registration')
            };
        } catch (error) {
            console.error('WebAuthn error:', error);
            return {
                success: false,
                message: `WebAuthn error: ${error.message || 'Unknown error'}`
            };
        }
    }

    // Set max date to today for date of birth
    const dobInput = document.getElementById('dateOfBirth');
    const today = new Date().toISOString().split('T')[0];
    dobInput.setAttribute('max', today);

    // Handle form submission
    document.getElementById('registrationForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const submitBtn = document.getElementById('submitBtn');
        const responseMessage = document.getElementById('responseMessage');

        // Clear previous messages
        responseMessage.style.display = 'none';
        document.querySelectorAll('.error-message').forEach(el => el.style.display = 'none');

        // Validate inputs
        const employeeId = document.getElementById('employeeId').value;
        const dob = dobInput.value;
        const verificationCode = document.getElementById('verificationCode').value;

        let isValid = true;

        if (!employeeId || parseInt(employeeId) < 0) {
            document.getElementById('employeeIdError').style.display = 'block';
            isValid = false;
        }

        if (!dob || new Date(dob) > new Date()) {
            document.getElementById('dobError').style.display = 'block';
            isValid = false;
        }

        if (!verificationCode.match(/^\d{6}$/)) {
            document.getElementById('codeError').style.display = 'block';
            isValid = false;
        }

        if (!isValid) return;

        // Disable button and show loading
        submitBtn.disabled = true;
        submitBtn.querySelector('.loading').style.display = 'inline-block';

        try {
            // Step 1: Initiate registration
            responseMessage.textContent = 'Initiating registration...';
            responseMessage.className = 'success';
            responseMessage.style.display = 'block';

            const response = await fetch(`${backendUrl}/registration/initiate`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    employeeId: parseInt(employeeId),
                    dateOfBirth: dob,
                    verificationCode: verificationCode
                })
            });

            if (!response.ok) {
                const errorResult = await response.json();
                responseMessage.className = 'error';
                responseMessage.textContent = errorResult.message || 'Failed to initiate registration';
                return;
            }

            const initiationResponse = await response.json();

            // Step 2: Complete registration with WebAuthn
            responseMessage.textContent = 'Please follow the browser prompts to register your security key...';

            const completeResult = await completeRegistration(initiationResponse);

            responseMessage.className = completeResult.success ? 'success' : 'error';
            responseMessage.textContent = completeResult.message;

            if (completeResult.success) {
                document.getElementById('registrationForm').reset();
            }
        } catch (error) {
            console.error('Error:', error);
            responseMessage.className = 'error';
            responseMessage.textContent = 'Network error - please try again';
        } finally {
            submitBtn.disabled = false;
            submitBtn.querySelector('.loading').style.display = 'none';
            responseMessage.style.display = 'block';
        }
    });
});
