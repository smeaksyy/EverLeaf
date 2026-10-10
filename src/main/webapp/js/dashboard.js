// Everleaf Dashboard Interactive Controller
document.addEventListener('DOMContentLoaded', () => {
    // 1. Navigation switching
    const navItems = document.querySelectorAll('.nav-item[data-view]');
    const views = document.querySelectorAll('.content-body');
    const mobileToggle = document.getElementById('mobile-toggle');
    const sidebar = document.getElementById('sidebar');

    function switchView(targetView) {
        navItems.forEach(n => {
            if (n.getAttribute('data-view') === targetView) {
                n.classList.add('active');
            } else {
                n.classList.remove('active');
            }
        });

        views.forEach(v => {
            if (v.id === 'view-' + targetView) {
                v.style.display = 'block';
            } else {
                v.style.display = 'none';
            }
        });

        if (targetView === 'notes') {
            loadNotes();
        } else if (targetView === 'all-documents') {
            loadAllDocuments();
        } else if (targetView === 'recently-added') {
            loadRecentDocumentsView();
        } else if (targetView === 'favorites') {
            loadFavoritesView();
        } else if (targetView === 'categories') {
            loadCategories();
        }

        if (window.innerWidth <= 1024 && sidebar) {
            sidebar.classList.remove('open');
        }
    }

    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const targetView = item.getAttribute('data-view');
            if (targetView && targetView !== 'settings') {
                switchView(targetView);
            }
        });
    });

    if (mobileToggle) {
        mobileToggle.addEventListener('click', () => {
            sidebar.classList.toggle('open');
        });
    }

    // Browse Documents and Manage Categories Quick Action buttons
    document.getElementById('btn-browse-docs')?.addEventListener('click', () => {
        switchView('all-documents');
    });

    document.getElementById('btn-manage-categories')?.addEventListener('click', () => {
        switchView('categories');
    });

    // 2. Modals setup
    const docModal = document.getElementById('doc-modal');
    const deleteModal = document.getElementById('delete-modal');
    const catModal = document.getElementById('category-modal');
    const noteModal = document.getElementById('note-modal');

    // Document Modal opener
    document.getElementById('btn-add-doc')?.addEventListener('click', () => {
        document.getElementById('doc-form')?.reset();
        document.getElementById('doc-modal-title').textContent = 'Add New Document';
        loadCategories(); // Refresh categories in dropdown
        docModal?.classList.add('active');
    });

    // Note Modal opener
    document.getElementById('btn-quick-note')?.addEventListener('click', () => {
        openNoteModal();
    });

    document.getElementById('btn-add-note')?.addEventListener('click', () => {
        openNoteModal();
    });

    // Category Modal opener
    document.getElementById('btn-add-category')?.addEventListener('click', () => {
        document.getElementById('category-form')?.reset();
        catModal?.classList.add('active');
    });

    // Quick create category shortcuts from inside doc-modal & note-modal
    document.getElementById('btn-quick-create-cat')?.addEventListener('click', () => {
        document.getElementById('category-form')?.reset();
        catModal?.classList.add('active');
    });

    document.getElementById('btn-quick-create-cat-note')?.addEventListener('click', () => {
        document.getElementById('category-form')?.reset();
        catModal?.classList.add('active');
    });

    // Close buttons for all modals
    document.querySelectorAll('.close-modal, .cancel-modal').forEach(btn => {
        btn.addEventListener('click', () => {
            docModal?.classList.remove('active');
            deleteModal?.classList.remove('active');
            catModal?.classList.remove('active');
            noteModal?.classList.remove('active');
        });
    });

    // Close modal on background click
    [docModal, deleteModal, catModal, noteModal].forEach(m => {
        m?.addEventListener('click', (e) => {
            if (e.target === m) {
                m.classList.remove('active');
            }
        });
    });

    // 3. File Drag & Drop UI
    const dropZone = document.getElementById('file-drop-zone');
    const fileInput = document.getElementById('doc-file');
    
    if (dropZone && fileInput) {
        fileInput.addEventListener('change', () => {
            if (fileInput.files.length > 0) {
                dropZone.querySelector('p').textContent = fileInput.files[0].name;
            }
        });

        dropZone.addEventListener('dragover', (e) => {
            e.preventDefault();
            dropZone.classList.add('dragover');
        });

        dropZone.addEventListener('dragleave', () => {
            dropZone.classList.remove('dragover');
        });

        dropZone.addEventListener('drop', (e) => {
            e.preventDefault();
            dropZone.classList.remove('dragover');
            if (e.dataTransfer.files.length > 0) {
                fileInput.files = e.dataTransfer.files;
                dropZone.querySelector('p').textContent = fileInput.files[0].name;
            }
        });
    }

    // 4. Form Submissions
    // Upload Document form
    const docForm = document.getElementById('doc-form');
    if (docForm) {
        docForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const formData = new FormData(docForm);
            fetch('/everleaf/documents', {
                method: 'POST',
                body: formData
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    showToast('Document uploaded successfully!');
                    docModal?.classList.remove('active');
                    loadDashboardData();
                } else {
                    showToast('Failed to upload document. Verify file type and size.', 'error');
                }
            })
            .catch(() => showToast('Error communicating with server', 'error'));
        });
    }

    // Category form
    const categoryForm = document.getElementById('category-form');
    if (categoryForm) {
        categoryForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const catName = document.getElementById('cat-name')?.value;
            const csrfToken = document.querySelector('.csrf-input')?.value;
            const params = new URLSearchParams();
            params.append('categoryName', catName);
            params.append('csrf_token', csrfToken || '');

            fetch('/everleaf/categories', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: params.toString()
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    showToast(`Category "${catName}" created!`);
                    catModal?.classList.remove('active');
                    loadCategories();
                    loadDashboardData();
                } else {
                    showToast('Could not create category', 'error');
                }
            })
            .catch(() => showToast('Server error', 'error'));
        });
    }

    // Note form (Leaf Notes & Ideas)
    const noteForm = document.getElementById('note-form');
    if (noteForm) {
        noteForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const noteId = document.getElementById('note-id')?.value;
            const title = document.getElementById('note-title')?.value;
            const content = document.getElementById('note-content')?.value;
            const categoryId = document.getElementById('note-category')?.value;
            const colorInput = document.querySelector('input[name="note-color"]:checked');
            const color = colorInput ? colorInput.value : '#FEF9C3';
            const csrfToken = document.querySelector('.csrf-input')?.value;

            const params = new URLSearchParams();
            params.append('title', title);
            params.append('content', content);
            params.append('categoryId', categoryId || '');
            params.append('color', color);
            params.append('csrf_token', csrfToken || '');

            let url = '/everleaf/notes';
            if (noteId) {
                params.append('noteId', noteId);
                url = '/everleaf/notes/update';
            }

            fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: params.toString()
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    showToast(noteId ? 'Note updated!' : 'Leaf Note saved to your space!');
                    noteModal?.classList.remove('active');
                    loadNotes();
                } else {
                    showToast('Failed to save note', 'error');
                }
            })
            .catch(() => showToast('Server connection failed', 'error'));
        });
    }

    // 5. Toast system
    window.showToast = function(message, type = 'success') {
        const container = document.getElementById('toast-container');
        if (!container) return;
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        
        let icon = type === 'success' 
            ? '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>'
            : '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line></svg>';
            
        toast.innerHTML = `${icon} <span>${message}</span>`;
        container.appendChild(toast);
        
        setTimeout(() => toast.classList.add('show'), 10);
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 300);
        }, 4000);
    };

    // 6. Global Search
    const searchInput = document.getElementById('global-search');
    if (searchInput) {
        let debounceTimer;
        searchInput.addEventListener('input', () => {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => {
                const query = searchInput.value.trim();
                if (query.length > 0) {
                    fetch(`/everleaf/documents?search=${encodeURIComponent(query)}`)
                        .then(res => res.json())
                        .then(docs => {
                            renderDocuments(docs, 'documents-tbody');
                            document.getElementById('empty-state').style.display = docs.length === 0 ? 'flex' : 'none';
                            document.getElementById('documents-table-container').style.display = docs.length === 0 ? 'none' : 'block';
                        })
                        .catch(err => console.log('Search error:', err));
                } else {
                    loadDashboardData();
                }
            }, 300);
        });
    }

    // 7. Logout
    document.getElementById('logout-btn')?.addEventListener('click', (e) => {
        e.preventDefault();
        window.location.href = '/everleaf/logout';
    });

    // 8. Initial App Loads
    loadDashboardData();
    loadCategories();
    loadNotes();
    
    // Fetch and populate CSRF Token
    fetch('/everleaf/csrf')
        .then(res => res.json())
        .then(data => {
            if (data.token) {
                document.querySelectorAll('.csrf-input').forEach(el => el.value = data.token);
            }
        })
        .catch(e => console.log('CSRF missing'));
});

// Helper: Open Note Modal
function openNoteModal(note = null) {
    const modal = document.getElementById('note-modal');
    const form = document.getElementById('note-form');
    if (!modal || !form) return;

    form.reset();
    loadCategories(); // Ensure latest categories are in dropdown

    if (note) {
        document.getElementById('note-modal-title').textContent = '✏️ Edit Leaf Note';
        document.getElementById('note-id').value = note.noteId;
        document.getElementById('note-title').value = note.title;
        document.getElementById('note-content').value = note.content || '';
        if (note.categoryId) {
            document.getElementById('note-category').value = note.categoryId;
        }
        const colorRadio = form.querySelector(`input[name="note-color"][value="${note.color}"]`);
        if (colorRadio) colorRadio.checked = true;
    } else {
        document.getElementById('note-modal-title').textContent = '✍️ New Leaf Note';
        document.getElementById('note-id').value = '';
    }
    modal.classList.add('active');
}

// Load Dashboard Summary Data
function loadDashboardData() {
    fetch('/everleaf/dashboard')
        .then(res => {
            if (res.ok) return res.json();
            throw new Error('API not ready');
        })
        .then(data => {
            if (data.user) {
                document.getElementById('greeting').textContent = `Welcome back, ${data.user.name}!`;
                document.getElementById('user-avatar').textContent = data.user.name.charAt(0).toUpperCase();
            }
            if (data.stats) {
                document.getElementById('stat-total-docs').textContent = data.stats.totalDocs || 0;
                document.getElementById('stat-categories').textContent = data.stats.categories || 0;
                document.getElementById('stat-recent').textContent = data.stats.recent || 0;
                document.getElementById('stat-favorites').textContent = data.stats.favorites || 0;
            }
            if (data.recentDocuments && data.recentDocuments.length > 0) {
                renderDocuments(data.recentDocuments, 'documents-tbody');
                document.getElementById('empty-state').style.display = 'none';
                document.getElementById('documents-table-container').style.display = 'block';
            } else {
                document.getElementById('empty-state').style.display = 'flex';
                document.getElementById('documents-table-container').style.display = 'none';
            }
        })
        .catch(err => {
            console.log('Error loading dashboard data:', err);
        });
}

// Load Categories from Backend API and populate selects & category cards
function loadCategories() {
    fetch('/everleaf/categories')
        .then(res => {
            if (res.ok) return res.json();
            throw new Error('API not ready');
        })
        .then(data => {
            if (!Array.isArray(data) || data.length === 0) {
                return;
            }

            const selectDoc = document.getElementById('doc-category');
            const selectNote = document.getElementById('note-category');
            const container = document.getElementById('categories-container');

            if (selectDoc) {
                selectDoc.innerHTML = '<option value="">Select a category</option>';
                data.forEach(c => {
                    selectDoc.innerHTML += `<option value="${c.categoryId}">${c.categoryName}</option>`;
                });
            }

            if (selectNote) {
                selectNote.innerHTML = '<option value="">(Uncategorized / Quick Memo)</option>';
                data.forEach(c => {
                    selectNote.innerHTML += `<option value="${c.categoryId}">${c.categoryName}</option>`;
                });
            }

            if (container) {
                container.innerHTML = '';
                data.forEach(c => {
                    const card = document.createElement('div');
                    card.className = 'stat-card';
                    card.style.cursor = 'default';
                    card.innerHTML = `
                        <div class="stat-icon icon-categories">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"></path></svg>
                        </div>
                        <div class="stat-details">
                            <span class="stat-value" style="font-size: 1.25rem;">${c.categoryName}</span>
                            <span class="stat-label">Organized Collection</span>
                        </div>
                    `;
                    container.appendChild(card);
                });
            }
        })
        .catch(e => console.log('Error fetching categories:', e));
}

// Load Leaf Notes & Ideas
function loadNotes() {
    fetch('/everleaf/notes')
        .then(res => {
            if (res.ok) return res.json();
            throw new Error('Notes API error');
        })
        .then(notes => {
            const grid = document.getElementById('notes-grid');
            const emptyState = document.getElementById('notes-empty-state');
            if (!grid) return;

            grid.innerHTML = '';
            if (!notes || notes.length === 0) {
                if (emptyState) emptyState.style.display = 'block';
                return;
            }

            if (emptyState) emptyState.style.display = 'none';

            notes.forEach(note => {
                const card = document.createElement('div');
                card.className = 'note-card';
                card.style.backgroundColor = note.color || '#FEF9C3';

                const catBadge = note.categoryName 
                    ? `<span class="note-card-category">📁 ${note.categoryName}</span>` 
                    : `<span class="note-card-category" style="opacity: 0.6;">📌 Quick Note</span>`;

                card.innerHTML = `
                    <div>
                        <div class="note-card-header">
                            <h3 class="note-card-title">${escapeHtml(note.title)}</h3>
                            <div class="note-actions">
                                <button class="icon-btn edit-note" title="Edit" style="background: rgba(255,255,255,0.7); padding: 4px; border-radius: 6px;">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14"><path d="M12 20h9"></path><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path></svg>
                                </button>
                                <button class="icon-btn delete delete-note" title="Delete" style="background: rgba(255,255,255,0.7); padding: 4px; border-radius: 6px;">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg>
                                </button>
                            </div>
                        </div>
                        ${catBadge}
                        <div class="note-card-body">${escapeHtml(note.content || '')}</div>
                    </div>
                    <div class="note-card-footer">
                        <span>🌱 Everleaf Idea</span>
                        <span>${note.updatedAt ? new Date().toLocaleDateString() : 'Active'}</span>
                    </div>
                `;

                card.querySelector('.edit-note').addEventListener('click', () => openNoteModal(note));
                card.querySelector('.delete-note').addEventListener('click', () => deleteNote(note.noteId, note.title));

                grid.appendChild(card);
            });
        })
        .catch(err => console.log('Error loading notes:', err));
}

// Delete Note
function deleteNote(noteId, title) {
    if (!confirm(`Are you sure you want to delete note "${title}"?`)) return;
    const csrfToken = document.querySelector('.csrf-input')?.value;
    const params = new URLSearchParams();
    params.append('noteId', noteId);
    params.append('csrf_token', csrfToken || '');

    fetch('/everleaf/notes/delete', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString()
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            showToast('Note deleted');
            loadNotes();
        } else {
            showToast('Could not delete note', 'error');
        }
    })
    .catch(() => showToast('Server error', 'error'));
}

// Load All Documents View
function loadAllDocuments() {
    fetch('/everleaf/documents')
        .then(res => res.json())
        .then(docs => {
            renderDocuments(docs, 'all-documents-tbody');
        })
        .catch(err => console.log('Error loading all documents:', err));
}

// Load Recent Documents View
function loadRecentDocumentsView() {
    fetch('/everleaf/documents')
        .then(res => res.json())
        .then(docs => {
            renderDocuments(docs.slice(0, 10), 'recent-documents-tbody');
        })
        .catch(err => console.log('Error loading recent view:', err));
}

// Load Favorites View
function loadFavoritesView() {
    fetch('/everleaf/documents')
        .then(res => res.json())
        .then(docs => {
            const favs = docs.filter(d => d.favorite);
            renderDocuments(favs, 'favorites-documents-tbody');
        })
        .catch(err => console.log('Error loading favorites view:', err));
}

// Toggle Document Favorite
function toggleFavorite(docId, currentStatus) {
    const csrfToken = document.querySelector('.csrf-input')?.value;
    const params = new URLSearchParams();
    params.append('documentId', docId);
    params.append('favorite', (!currentStatus).toString());
    params.append('csrf_token', csrfToken || '');

    fetch('/everleaf/documents/favorite', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString()
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            showToast(!currentStatus ? 'Added to favorites ⭐' : 'Removed from favorites');
            loadDashboardData();
        }
    })
    .catch(err => console.log('Favorite toggle error:', err));
}

// Helper: Render Documents Table
function renderDocuments(docs, tbodyId = 'documents-tbody') {
    const tbody = document.getElementById(tbodyId);
    if (!tbody) return;
    tbody.innerHTML = '';

    if (!docs || docs.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: var(--secondary-text); padding: 2rem;">No documents in this view yet.</td></tr>';
        return;
    }

    docs.forEach(doc => {
        const tr = document.createElement('tr');
        const isFav = !!doc.favorite;
        tr.innerHTML = `
            <td>
                <div style="display: flex; align-items: center; gap: 8px;">
                    <button class="icon-btn fav-btn" title="${isFav ? 'Unfavorite' : 'Favorite'}" style="color: ${isFav ? '#F59E0B' : '#9CA3AF'};">
                        <svg viewBox="0 0 24 24" fill="${isFav ? '#F59E0B' : 'none'}" stroke="currentColor" stroke-width="2" width="16" height="16"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
                    </button>
                    <strong>${escapeHtml(doc.title)}</strong>
                </div>
            </td>
            <td><span class="badge-cat">${escapeHtml(doc.categoryName || 'General')}</span></td>
            <td>${doc.uploadDate ? new Date().toLocaleDateString() : 'Today'}</td>
            <td><span class="badge-type">${doc.mimeType ? doc.mimeType.split('/').pop().toUpperCase() : 'FILE'}</span></td>
            <td>
                <div class="action-btns">
                    <button class="icon-btn" onclick="window.location.href='/everleaf/download?documentId=${doc.documentId}'" title="Download">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
                    </button>
                    <button class="icon-btn delete" onclick="confirmDelete(${doc.documentId}, '${escapeHtml(doc.title)}')" title="Delete">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg>
                    </button>
                </div>
            </td>
        `;

        tr.querySelector('.fav-btn').addEventListener('click', () => toggleFavorite(doc.documentId, isFav));
        tbody.appendChild(tr);
    });
}

// Delete Document Confirmation
window.confirmDelete = function(id, title) {
    document.getElementById('delete-doc-name').textContent = title;
    const modal = document.getElementById('delete-modal');
    modal.classList.add('active');
    
    const confirmBtn = document.getElementById('confirm-delete-btn');
    confirmBtn.onclick = function() {
        const csrfToken = document.querySelector('.csrf-input')?.value;
        const params = new URLSearchParams();
        params.append('documentId', id);
        params.append('csrf_token', csrfToken || '');

        fetch('/everleaf/documents/delete', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: params.toString()
        })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                showToast('Document deleted');
                modal.classList.remove('active');
                loadDashboardData();
            } else {
                showToast('Failed to delete document', 'error');
            }
        })
        .catch(() => showToast('Server error', 'error'));
    };
};

// Safe HTML String Escape
function escapeHtml(str) {
    if (!str) return '';
    return str
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
