<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { wishlistApi, type WishlistItem } from '@/api/wishlist'
import AddWishlistModal from '@/components/AddWishlistModal.vue'

const router = useRouter()
const wishlist = ref<WishlistItem[]>([])
const loading = ref(true)
const showAddWishlist = ref(false)

async function loadWishlist() {
  loading.value = true
  try {
    const { data } = await wishlistApi.list()
    wishlist.value = data
  } finally {
    loading.value = false
  }
}

function onWishlistAdded() {
  showAddWishlist.value = false
  loadWishlist()
}

async function removeWishlistItem(id: number) {
  if (!confirm('ほしいものリストから削除しますか？')) return
  try {
    await wishlistApi.delete(id)
    wishlist.value = wishlist.value.filter((w) => w.id !== id)
  } catch {
    alert('削除に失敗しました')
  }
}

function goToMypage() {
  router.push({ name: 'mypage' })
}

onMounted(() => {
  loadWishlist()
})
</script>

<template>
  <div class="wishlist-page">
    <header class="header">
      <h1>ほしいものリスト</h1>
      <div class="nav">
        <button type="button" class="btn-link" @click="goToMypage">マイページへ</button>
      </div>
    </header>

    <section class="section">
      <div class="section-head">
        <h2>一覧</h2>
        <button type="button" class="btn-primary" @click="showAddWishlist = true">追加</button>
      </div>

      <div v-if="loading" class="loading">読み込み中...</div>
      <div v-else-if="wishlist.length === 0" class="empty">
        ほしいものはまだありません。「追加」から登録してください。
      </div>
      <div v-else class="game-grid">
        <div v-for="w in wishlist" :key="w.id" class="game-card">
          <div class="game-thumb">
            <img
              v-if="w.thumbnailUrl"
              :src="w.thumbnailUrl"
              :alt="w.name"
              loading="lazy"
            />
            <div v-else class="no-image">No image</div>
          </div>
          <div class="game-body">
            <h3 class="game-name">{{ w.name }}</h3>
            <div class="game-actions">
              <button type="button" class="btn-sm btn-danger" @click="removeWishlistItem(w.id)">削除</button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <AddWishlistModal
      v-if="showAddWishlist"
      @close="showAddWishlist = false"
      @added="onWishlistAdded"
    />
  </div>
</template>

<style scoped>
.wishlist-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 1rem;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e5e7eb;
}
.header h1 {
  font-size: 1.5rem;
}
.nav {
  display: flex;
  align-items: center;
  gap: 1rem;
}
.btn-link {
  padding: 0.4rem 0.75rem;
  background: none;
  border: none;
  color: #2563eb;
  font-size: 0.9rem;
  text-decoration: underline;
  cursor: pointer;
}
.btn-link:hover {
  color: #1d4ed8;
}
.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}
.section-head h2 {
  font-size: 1.25rem;
}
.btn-primary {
  padding: 0.5rem 1rem;
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 4px;
}
.btn-primary:hover {
  background: #1d4ed8;
}
.loading, .empty {
  padding: 2rem;
  text-align: center;
  color: #6b7280;
}
.game-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1rem;
}
.game-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}
.game-thumb {
  aspect-ratio: 1;
  background: #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: center;
}
.game-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.no-image {
  color: #9ca3af;
  font-size: 0.9rem;
}
.game-body {
  padding: 0.75rem;
}
.game-name {
  font-size: 1rem;
  margin-bottom: 0.5rem;
  line-height: 1.3;
}
.game-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 0.5rem;
}
.btn-sm {
  padding: 0.25rem 0.5rem;
  font-size: 0.8rem;
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
}
.btn-sm:hover {
  background: #e5e7eb;
}
.btn-danger {
  color: #b91c1c;
  border-color: #fecaca;
}
.btn-danger:hover {
  background: #fee2e2;
}
</style>
