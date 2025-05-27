function showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    let title = '提示';
    let headerClass = 'bg-primary text-white';

    if (type === 'error') {
        title = '错误';
        headerClass = 'bg-danger text-white';
    } else if (type === 'info') {
        title = '通知';
        headerClass = 'bg-info text-white';
    } else if (type === 'warning') {
        title = '警告';
        headerClass = 'bg-warning text-dark';
    } else if (type === 'success') {
        title = '成功';
        headerClass = 'bg-body-secondary text-dark'; // ✅ 改成灰色背景 + 黑字
    }

    const toast = document.createElement('div');
    toast.className = `toast border-0 shadow-sm bg-white text-dark`;  // border-0 to remove border
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');

    toast.innerHTML = `
        <div class="toast-header ${headerClass} border-0">
            <strong class="me-auto">${title}</strong>
            <small class="text-white-50">刚刚</small>
            <button type="button" class="btn-close ${headerClass.includes('text-dark') ? '' : 'btn-close-white'}" data-bs-dismiss="toast" aria-label="关闭"></button>
        </div>
        <div class="toast-body fw-semibold">
            ${message}
        </div>
    `;

    container.appendChild(toast);
    const bsToast = new bootstrap.Toast(toast, { delay: 3000 });
    bsToast.show();
    toast.addEventListener('hidden.bs.toast', () => toast.remove());
}
