document.addEventListener('DOMContentLoaded', () => {
    // --- STATE MANAGEMENT ---
    const state = {
        currentUser: null,
        cart: [],
        currentCustomer: null,
    };

    // --- DOM ELEMENTS ---
    const loginView = document.getElementById('login-view');
    const posView = document.getElementById('pos-view');
    const adminView = document.getElementById('admin-view');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const loginBtn = document.getElementById('login-btn');
    const logoutBtn = document.getElementById('logout-btn');
    const loginError = document.getElementById('login-error');
    const currentUserSpan = document.getElementById('currentUser');
    const productSearchInput = document.getElementById('product-search');
    const productSearchBtn = document.getElementById('product-search-btn');
    const productSearchResults = document.getElementById('product-search-results');
    const cartItemsList = document.getElementById('cart-items');
    const totalPriceSpan = document.getElementById('total-price');
    const customerPhoneInput = document.getElementById('customer-phone');
    const customerLookupBtn = document.getElementById('customer-lookup-btn');
    const customerInfoDiv = document.getElementById('customer-info');
    const newCustomerNameInput = document.getElementById('new-customer-name');
    const newCustomerPhoneInput = document.getElementById('new-customer-phone');
    const createCustomerBtn = document.getElementById('create-customer-btn');
    const createOrderBtn = document.getElementById('create-order-btn');

    // --- API HELPER ---
    const apiRequest = async (endpoint, options = {}) => {
        try {
            const response = await fetch(`/api${endpoint}`, options);
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || response.statusText);
            }
            if (response.status === 204 || (response.status === 200 && response.headers.get("Content-Length") === "0")) {
                return null;
            }
            return response.json();
        } catch (error) {
            console.error(`API Error on ${endpoint}:`, error);
            throw error;
        }
    };

    // --- LOGIC FUNCTIONS ---
    const login = async () => {
        const username = usernameInput.value;
        const password = passwordInput.value;
        loginError.textContent = '';
        try {
            const user = await apiRequest('/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password }),
            });
            state.currentUser = user;
            updateUI();
        } catch (error) {
            loginError.textContent = 'Invalid username or password.';
        }
    };

    const logout = async () => {
        try {
            await apiRequest('/auth/logout', { method: 'POST' });
            state.currentUser = null;
            state.cart = [];
            state.currentCustomer = null;
            renderCart();
            renderCustomerInfo();
            updateUI();
        } catch (error) {
            console.error('Logout failed:', error);
        }
    };

    // CORRECTED: Added the missing searchProducts function
    const searchProducts = async () => {
        const searchTerm = productSearchInput.value.trim();
        if (searchTerm.length < 2) {
            renderSearchResults([]); // Clear results if search is too short
            return;
        }
        try {
            const products = await apiRequest(`/products/search?name=${searchTerm}`);
            renderSearchResults(products);
        } catch (error) {
            console.error('Failed to search products:', error);
            renderSearchResults([]); // Clear results on error
        }
    };

    const addToCart = (product) => {
        const existingItem = state.cart.find(item => item.id === product.id);
        if (existingItem) {
            existingItem.quantity++;
        } else {
            state.cart.push({
                id: product.id,
                name: product.name,
                price: product.price,
                quantity: 1,
            });
        }
        renderCart();
    };

    const lookupCustomer = async () => {
        const phoneNumber = customerPhoneInput.value.trim();
        if (!phoneNumber) return;
        try {
            const customer = await apiRequest(`/customers/lookup/by-phone/${phoneNumber}`);
            state.currentCustomer = customer;
            renderCustomerInfo();
        } catch (error) {
            alert('Customer not found.');
            state.currentCustomer = null;
            renderCustomerInfo();
        }
    };

    const createCustomer = async () => {
        const name = newCustomerNameInput.value.trim();
        const phoneNumber = newCustomerPhoneInput.value.trim();
        if (!name || !phoneNumber) {
            alert('Please provide both name and phone number for the new customer.');
            return;
        }
        try {
            const newCustomer = await apiRequest('/customers', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, phoneNumber }),
            });
            state.currentCustomer = newCustomer;
            renderCustomerInfo();
            newCustomerNameInput.value = '';
            newCustomerPhoneInput.value = '';
        } catch (error) {
            alert('Failed to create customer. The phone number may already be in use.');
        }
    };

    const createOrder = async () => {
        if (state.cart.length === 0) {
            alert('Cannot create an empty order.');
            return;
        }
        const orderData = {
            items: state.cart.map(item => ({
                productId: item.id,
                quantity: item.quantity,
            })),
            customerPhoneNumber: state.currentCustomer ? state.currentCustomer.phoneNumber : null,
        };
        try {
            const newOrder = await apiRequest('/orders', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(orderData),
            });
            alert(`Order #${newOrder.id} created successfully!`);
            state.cart = [];
            state.currentCustomer = null;
            renderCart();
            renderCustomerInfo();
        } catch (error) {
            alert(`Failed to create order: ${error.message}`);
        }
    };

    // --- UI FUNCTIONS ---
    const updateUI = () => {
        if (state.currentUser) {
            loginView.classList.add('d-none');
            posView.classList.remove('d-none');
            currentUserSpan.textContent = state.currentUser.username;
            if (state.currentUser.role === 'ADMIN') {
                adminView.classList.remove('d-none');
            } else {
                adminView.classList.add('d-none');
            }
        } else {
            loginView.classList.remove('d-none');
            posView.classList.add('d-none');
            usernameInput.value = 'staff';
            passwordInput.value = 'password';
        }
    };

    const renderSearchResults = (products) => {
        productSearchResults.innerHTML = '';
        if (products.length === 0) {
            productSearchResults.innerHTML = '<li class="list-group-item">No products found.</li>';
            return;
        }
        products.forEach(product => {
            const li = document.createElement('li');
            li.className = 'list-group-item d-flex justify-content-between align-items-center';
            li.innerHTML = `
                <div>
                    <span class="fw-bold">${product.name}</span>
                    <small class="text-muted ms-2">$${product.price.toFixed(2)}</small>
                </div>
                <button class="btn btn-sm btn-success add-to-cart-btn" data-product-id="${product.id}">Add</button>
            `;
            productSearchResults.appendChild(li);
        });
    };

    const renderCart = () => {
        cartItemsList.innerHTML = '';
        let totalPrice = 0;
        if (state.cart.length === 0) {
            cartItemsList.innerHTML = '<li class="list-group-item">Cart is empty</li>';
        } else {
            state.cart.forEach(item => {
                const itemTotal = item.price * item.quantity;
                totalPrice += itemTotal;
                const li = document.createElement('li');
                li.className = 'list-group-item';
                li.textContent = `${item.name} (x${item.quantity}) - $${itemTotal.toFixed(2)}`;
                cartItemsList.appendChild(li);
            });
        }
        totalPriceSpan.textContent = `$${totalPrice.toFixed(2)}`;
    };

    const renderCustomerInfo = () => {
        if (state.currentCustomer) {
            customerInfoDiv.innerHTML = `
                <div class="alert alert-info">
                    <p class="mb-1"><strong>Customer:</strong> ${state.currentCustomer.name}</p>
                    <p class="mb-0"><strong>Points:</strong> ${state.currentCustomer.points}</p>
                    <button id="clear-customer-btn" class="btn btn-sm btn-outline-danger mt-2">Clear</button>
                </div>
            `;
            customerPhoneInput.value = '';
        } else {
            customerInfoDiv.innerHTML = '';
        }
    };

    // --- EVENT LISTENERS ---
    loginBtn.addEventListener('click', login);
    logoutBtn.addEventListener('click', logout);
    passwordInput.addEventListener('keypress', e => { if (e.key === 'Enter') login(); });
    productSearchBtn.addEventListener('click', searchProducts);
    productSearchInput.addEventListener('keypress', e => { if (e.key === 'Enter') searchProducts(); });

    productSearchResults.addEventListener('click', e => {
        if (e.target && e.target.classList.contains('add-to-cart-btn')) {
            const productId = e.target.getAttribute('data-product-id');
            const productName = e.target.closest('.list-group-item').querySelector('.fw-bold').textContent;
            const productPriceText = e.target.closest('.list-group-item').querySelector('.text-muted').textContent;
            const productPrice = parseFloat(productPriceText.replace('$', ''));
            addToCart({ id: parseInt(productId, 10), name: productName, price: productPrice });
            productSearchResults.innerHTML = '';
            productSearchInput.value = '';
        }
    });

    // CORRECTED: Moved these listeners out of the productSearchResults listener
    customerLookupBtn.addEventListener('click', lookupCustomer);
    customerPhoneInput.addEventListener('keypress', e => { if (e.key === 'Enter') lookupCustomer(); });
    createCustomerBtn.addEventListener('click', createCustomer);
    createOrderBtn.addEventListener('click', createOrder);

    customerInfoDiv.addEventListener('click', e => {
        if (e.target && e.target.id === 'clear-customer-btn') {
            state.currentCustomer = null;
            renderCustomerInfo();
        }
    });

    // --- INITIALIZATION ---
    updateUI();
    renderCart();
});