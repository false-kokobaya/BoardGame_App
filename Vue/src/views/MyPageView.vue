<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { boardgamesApi, type UserBoardGame } from '@/api/boardgames'
import AddGameModal from '@/components/AddGameModal.vue'
import EditGameModal from '@/components/EditGameModal.vue'

const auth = useAuthStore()
const router = useRouter()
const games = ref<UserBoardGame[]>([])
const loading = ref(true)
const showAddGame = ref(false)
const showEditGame = ref(false)
const gameToEdit = ref<UserBoardGame | null>(null)

async function loadGames() {
  loading.value = true
  try {
    const { data } = await boardgamesApi.list()
    games.value = data
  } finally {
    loading.value = false
  }
}

async function removeGame(id: number) {
  if (!confirm('このゲームを一覧から削除しますか？\n（紐づくプレイ記録も一緒に削除されます）')) return
  try {
    await boardgamesApi.delete(id)
    games.value = games.value.filter((g) => g.id !== id)
  } catch (e: unknown) {
    const err = e as { response?: { status?: number; data?: { error?: string } } }
    const msg = err.response?.status === 404
      ? '削除のURLが見つかりません。バックエンドを再起動して試してください。'
      : (err.response?.data?.error ?? '削除に失敗しました')
    alert(msg)
  }
}

function onGameAdded() {
  showAddGame.value = false
  loadGames()
}

function openEditGame(game: UserBoardGame) {
  gameToEdit.value = game
  showEditGame.value = true
}

function onGameUpdated() {
  showEditGame.value = false
  gameToEdit.value = null
  loadGames()
}

function closeEditGame() {
  showEditGame.value = false
  gameToEdit.value = null
}

function logout() {
  auth.clearAuth()
  router.push({ name: 'login' })
}

onMounted(() => {
  loadGames()
})
</script>

<template>
  <div class="mypage">
    <header class="header">
      <h1>マイページ</h1>
      <div class="user">
        <router-link :to="{ name: 'wishlist' }" class="btn-link">ほしいものリスト</router-link>
        <router-link :to="{ name: 'plays' }" class="btn-link">プレイ記録</router-link>
        <span>{{ auth.username }}</span>
        <button type="button" class="btn-logout" @click="logout">ログアウト</button>
      </div>
    </header>

    <section class="section">
      <div class="section-head">
        <h2>所持ゲーム一覧</h2>
        <button type="button" class="btn-primary" @click="showAddGame = true">追加</button>
      </div>

      <div v-if="loading" class="loading">読み込み中...</div>
      <div v-else-if="games.length === 0" class="empty">
        所持ゲームがありません。「ゲームを手動で追加」から追加してください。
      </div>
      <div v-else class="game-grid">
        <div v-for="g in games" :key="g.id" class="game-card">
          <div class="game-thumb">
            <img
              v-if="g.thumbnailUrl"
              :src="g.thumbnailUrl"
              :alt="g.name"
              loading="lazy"
            />
            <div v-else class="no-image">No image</div>
          </div>
          <div class="game-body">
            <h3 class="game-name">{{ g.name }}</h3>
            <div class="game-meta">
              <span v-if="g.yearPublished">{{ g.yearPublished }}年</span>
              <span v-if="g.minPlayers != null && g.maxPlayers != null">
                プレイ人数: {{ g.minPlayers }}-{{ g.maxPlayers }}人
              </span>
              <span v-if="g.minPlayTimeMinutes != null">
                プレイ時間: {{ g.minPlayTimeMinutes }}分
                <template v-if="g.maxPlayTimeMinutes != null && g.maxPlayTimeMinutes !== g.minPlayTimeMinutes">
                  ～{{ g.maxPlayTimeMinutes }}分
                </template>
              </span>
            </div>
            <div class="game-actions">
              <button type="button" class="btn-sm" @click="openEditGame(g)">情報を登録・編集</button>
              <button type="button" class="btn-sm btn-danger" @click="removeGame(g.id)">削除</button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <AddGameModal
      v-if="showAddGame"
      @close="showAddGame = false"
      @added="onGameAdded"
    />
    <EditGameModal
      v-if="showEditGame"
      :game="gameToEdit"
      @close="closeEditGame"
      @updated="onGameUpdated"
    />
  </div>
</template>

<style scoped>
.mypage {
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
.user {
  display: flex;
  align-items: center;
  gap: 1rem;
}
.btn-link {
  color: #2563eb;
  font-size: 0.9rem;
  text-decoration: none;
}
.btn-link:hover {
  text-decoration: underline;
}
.btn-logout {
  padding: 0.4rem 0.75rem;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 0.9rem;
}
.btn-logout:hover {
  background: #e5e7eb;
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
.game-meta {
  font-size: 0.8rem;
  color: #6b7280;
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
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
