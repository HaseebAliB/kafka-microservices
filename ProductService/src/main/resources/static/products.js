const PAGE_SIZE = 50;           // number of items per page
let scrollLimit = null;         // max items to load via infinite scroll
let totalItems = null;          // total products in DB (for pagination)
let loadedCount = 0;            // counter for infinite scroll
let currentPage = 1;            // current page for pagination
let nextPageToLoad = 1;         // next page to load for infinite scroll
let isLoading = false;
let reachedLimit = false;

const container = document.getElementById("product-list");
const paginationDiv = document.getElementById("pagination");

// Fetch products from API
async function fetchProducts(page) {
    const t0 = performance.now();
    const res = await fetch(`/api/products?page=${page}&size=${PAGE_SIZE}`);
    const data = await res.json();
    const t1 = performance.now();

    if (scrollLimit === null) scrollLimit = data.scrollLimit;
    if (totalItems === null) totalItems = data.totalItems;

    document.getElementById("latency").innerText =
        `End-to-End Render Time = ${(t1 - t0).toFixed(2)} ms`;

    return data;
}

// Render product list
function renderProducts(products, clear = false) {
    if (clear) container.innerHTML = "";

    products.forEach(p => {
        const div = document.createElement("div");
        div.className = "product";

        div.innerHTML = `
            <div>
                <b>${p.name}</b><br>
                Price: $${p.price}<br>
             </div>
            <button class="view-btn" onclick="viewProduct('${p.id}')">
                View
            </button>
        `;

        container.appendChild(div);
    });
}

function viewProduct(productId,quantity) {
    window.location.href = `/product-detail.html?id=${productId}`;
}

// Render pagination buttons
function renderPagination() {
    const totalPages = Math.ceil(totalItems / PAGE_SIZE);
    paginationDiv.innerHTML = "";

    for (let i = 1; i <= totalPages; i++) {
        const btn = document.createElement("span");
        btn.className = "page-btn";
        if (i === currentPage) btn.classList.add("active");
        btn.innerText = i;
        btn.onclick = () => {
            currentPage = i;
            nextPageToLoad = i + 1;     // next page for infinite scroll
            reachedLimit = false;        // allow scrolling
            loadedCount = (i - 1) * PAGE_SIZE;
            loadPage(i, true, false);   // clear container on page click
        };
        paginationDiv.appendChild(btn);
    }
}

// Load a page
async function loadPage(page, clear = false, isScroll = false) {
    if (isLoading) return;
    isLoading = true;

    const data = await fetchProducts(page);
    renderProducts(data.products, clear);

    if (isScroll) {
        loadedCount += data.products.length;
        if (loadedCount >= scrollLimit) reachedLimit = true;
    }

    renderPagination();
    isLoading = false;
}

// Infinite scroll
window.addEventListener("scroll", () => {
    if (reachedLimit || isLoading) return;

    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 150) {
        loadPage(nextPageToLoad, false, true);
        currentPage = nextPageToLoad;
        nextPageToLoad++;
    }
});

// Initial load
loadPage(1, true, true);
nextPageToLoad = 2;
