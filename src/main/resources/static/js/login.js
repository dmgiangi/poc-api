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

    // Prepare publicKey options for WebAuthn authentication
    function preparePublicKeyOptions(options) {
        return {
            ...options,
            challenge: base64UrlToArrayBuffer(options.challenge),
            allowCredentials: options.allowCredentials ?
                options.allowCredentials.map(cred => ({
                    ...cred,
                    id: base64UrlToArrayBuffer(cred.id)
                })) : []
        };
    }

    // Format authentication response for server
    function formatAuthResponseForServer(credential) {
        return {
            id: credential.id,
            rawId: arrayBufferToBase64Url(credential.rawId),
            response: {
                authenticatorData: arrayBufferToBase64Url(credential.response.authenticatorData),
                clientDataJSON: arrayBufferToBase64Url(credential.response.clientDataJSON),
                signature: arrayBufferToBase64Url(credential.response.signature),
                userHandle: credential.response.userHandle ?
                    arrayBufferToBase64Url(credential.response.userHandle) : null
            },
            clientExtensionResults: credential.getClientExtensionResults(),
            type: credential.type
        };
    }

    // Complete authentication with WebAuthn credential
    async function completeAuthentication(authOptions) {
        try {
            // Prepare publicKey options for WebAuthn
            const publicKeyOptions = preparePublicKeyOptions(authOptions.publicKey);

            // Get credential
            const credential = await navigator.credentials.get({
                publicKey: publicKeyOptions
            });

            // Format credential for server
            const formattedCredential = formatAuthResponseForServer(credential);

            // Send to server
            const completeResponse = await fetch(`${backendUrl}/authentication/complete/${authOptions.authRequestId}`, {
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
                    'Authentication successful!' :
                    (completeResult.message || 'Failed to authenticate')
            };
        } catch (error) {
            console.error('WebAuthn error:', error);
            return {
                success: false,
                message: `WebAuthn error: ${error.message || 'Unknown error'}`
            };
        }
    }

    // Handle form submission
    document.getElementById('loginForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const submitBtn = document.getElementById('submitBtn');
        const responseMessage = document.getElementById('responseMessage');

        // Clear previous messages
        responseMessage.style.display = 'none';
        document.querySelectorAll('.error-message').forEach(el => el.style.display = 'none');

        // Validate inputs
        const employeeId = document.getElementById('employeeId').value;

        let isValid = true;

        if (!employeeId || parseInt(employeeId) < 0) {
            document.getElementById('employeeIdError').style.display = 'block';
            isValid = false;
        }

        if (!isValid) return;

        // Disable button and show loading
        submitBtn.disabled = true;
        submitBtn.querySelector('.loading').style.display = 'inline-block';

        try {
            // Step 1: Initiate authentication
            responseMessage.textContent = 'Initiating authentication...';
            responseMessage.className = 'success';
            responseMessage.style.display = 'block';

            const response = await fetch(`${backendUrl}/authentication/initiate`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    employeeId: parseInt(employeeId)
                })
            });

            if (!response.ok) {
                const errorResult = await response.json();
                responseMessage.className = 'error';
                responseMessage.textContent = errorResult.message || 'Failed to initiate authentication';
                return;
            }

            const authOptions = await response.json();

            // Step 2: Complete authentication with WebAuthn
            responseMessage.textContent = 'Please follow the browser prompts to authenticate with your security key...';

            const completeResult = await completeAuthentication(authOptions);

            responseMessage.className = completeResult.success ? 'success' : 'error';
            responseMessage.textContent = completeResult.message;

            if (completeResult.success) {
                // Redirect to a protected page or show success message
                setTimeout(() => {
                    window.location.href = '/dashboard.html';
                }, 1500);
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