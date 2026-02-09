<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { boardgamesApi, type UserBoardGame } from '@/api/boardgames'
import { playsApi, type PlayRecord } from '@/api/plays'
import AddPlayRecordModal from '@/components/AddPlayRecordModal.vue'
import EditPlayRecordModal from '@/components/EditPlayRecordModal.vue'

const games = ref<UserBoardGame[]>([])
const plays = ref<PlayRecord[]>([])
const selectedGame = ref<UserBoardGame | null>(null)
const gamesLoading = ref(true)
const playsLoading = ref(false)
const showAddPlay = ref(false)
const playToEdit = ref<PlayRecord | null>(null)

async function loadGames() {
  gamesLoading.value = true
  try {
    const { data } = await boardgamesApi.list()
    games.value = data
    if (data.length > 0 && !selectedGame.value) {
      selectedGame.value = data[0]
    }
  } finally {
    gamesLoading.value = false
  }
}

async function loadPlays() {
  if (!selectedGame.value) {
    plays.value = []
    return
  }
  playsLoading.value = true
  try {
    const { data } = await playsApi.listByGame(selectedGame.value.id)
    plays.value = data
  } finally {
    playsLoading.value = false
  }
}

watch(selectedGame, () => {
  loadPlays()
}, { immediate: true })

function onPlayAdded() {
  showAddPlay.value = false
  loadPlays()
}

function closeAddPlay() {
  showAddPlay.value = false
}

function openEditPlay(play: PlayRecord) {
  playToEdit.value = play
}

function onPlayUpdated() {
  playToEdit.value = null
  loadPlays()
}

function closeEditPlay() {
  playToEdit.value = null
}

async function deletePlayRecord(play: PlayRecord) {
  if (!confirm('このプレイ記録を削除しますか？')) return
  try {
    await playsApi.delete(play.userBoardGameId, play.id)
    plays.value = plays.value.filter((p) => p.id !== play.id)
  } catch (e: unknown) {
    const err = e as { response?: { status?: number; data?: { error?: string } } }
    const msg = err.response?.status === 404
      ? '削除のURLが見つかりません。バックエンドを再起動して試してください。'
      : (err.response?.data?.error ?? '削除に失敗しました')
    alert(msg)
  }
}

function selectGame(game: UserBoardGame) {
  selectedGame.value = game
}

onMounted(() => {
  loadGames()
})
</script>

<template>
  <div class="plays-page">
    <header class="header">
      <h1>プレイ記録</h1>
      <div class="nav">
        <router-link :to="{ name: 'mypage' }" class="btn-link">マイページへ</router-link>
      </div>
    </header>

    <div v-if="gamesLoading" class="loading">読み込み中...</div>

    <template v-else-if="games.length === 0">
      <div class="empty-state">
        <p>所持ゲームがありません。</p>
        <p class="hint">マイページでゲームを追加すると、ここでプレイ記録をつけられます。</p>
        <router-link :to="{ name: 'mypage' }" class="btn-primary">マイページへ</router-link>
      </div>
    </template>

    <template v-else>
      <div class="layout">
        <aside class="game-picker">
          <h2 class="picker-title">ゲームを選択</h2>
          <div class="game-list">
            <button
              v-for="g in games"
              :key="g.id"
              type="button"
              class="game-pick-item"
              :class="{ active: selectedGame?.id === g.id }"
              @click="selectGame(g)"
            >
              <span class="game-pick-thumb">
                <img v-if="g.thumbnailUrl" :src="g.thumbnailUrl" :alt="g.name" />
                <span v-else class="no-img">?</span>
              </span>
              <span class="game-pick-name">{{ g.name }}</span>
            </button>
          </div>
        </aside>

        <main class="main">
          <template v-if="selectedGame">
            <div class="game-header">
              <div class="game-header-info">
                <div class="game-header-thumb">
                  <img v-if="selectedGame.thumbnailUrl" :src="selectedGame.thumbnailUrl" :alt="selectedGame.name" />
                  <span v-else class="no-img">?</span>
                </div>
                <div>
                  <h2 class="game-header-name">{{ selectedGame.name }}</h2>
                  <p v-if="selectedGame.yearPublished || selectedGame.minPlayers != null" class="game-header-meta">
                    <span v-if="selectedGame.yearPublished">{{ selectedGame.yearPublished }}年</span>
                    <span v-if="selectedGame.minPlayers != null && selectedGame.maxPlayers != null">
                      · {{ selectedGame.minPlayers }}-{{ selectedGame.maxPlayers }}人
                    </span>
                  </p>
                </div>
              </div>
              <button type="button" class="btn-primary" @click="showAddPlay = true">
                記録を追加
              </button>
            </div>

            <section class="records-section">
              <h3 class="records-title">プレイ履歴</h3>
              <div v-if="playsLoading" class="loading-inline">読み込み中...</div>
              <div v-else-if="plays.length === 0" class="empty-records">
                まだ記録がありません。「記録を追加」からプレイ日・人数・メモを登録できます。
              </div>
              <ul v-else class="records-list">
                <li v-for="p in plays" :key="p.id" class="record-item">
                  <div class="record-content">
                    <span class="record-date">{{ p.playedAt }}</span>
                    <span v-if="p.playerCount != null" class="record-count">{{ p.playerCount }}人でプレイ</span>
                    <p v-if="p.memo" class="record-memo">{{ p.memo }}</p>
                  </div>
                  <div class="record-actions">
                    <button type="button" class="btn-record btn-edit" @click="openEditPlay(p)">編集</button>
                    <button type="button" class="btn-record btn-delete" @click="deletePlayRecord(p)">削除</button>
                  </div>
                </li>
              </ul>
            </section>
          </template>
        </main>
      </div>
    </template>

    <AddPlayRecordModal
      v-if="showAddPlay && selectedGame"
      :user-board-game-id="selectedGame.id"
      @close="closeAddPlay"
      @added="onPlayAdded"
    />
    <EditPlayRecordModal
      v-if="playToEdit"
      :play="playToEdit"
      @close="closeEditPlay"
      @updated="onPlayUpdated"
    />
  </div>
</template>

<style scoped>
.plays-page {
  max-width: 1100px;
  margin: 0 auto;
  padding: 1rem;
  min-height: 60vh;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e5e7eb;
}
.header h1 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #111827;
}
.nav {
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
.loading {
  padding: 3rem;
  text-align: center;
  color: #6b7280;
}
.empty-state {
  text-align: center;
  padding: 3rem 1.5rem;
  background: #f9fafb;
  border-radius: 12px;
  border: 1px dashed #e5e7eb;
}
.empty-state p {
  margin: 0 0 0.5rem;
  color: #374151;
}
.empty-state .hint {
  font-size: 0.9rem;
  color: #6b7280;
  margin-bottom: 1.25rem;
}
.layout {
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 1.5rem;
  align-items: start;
}
@media (max-width: 700px) {
  .layout {
    grid-template-columns: 1fr;
  }
}
.game-picker {
  background: #fff;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
  padding: 0.75rem;
  position: sticky;
  top: 1rem;
}
.picker-title {
  font-size: 0.85rem;
  font-weight: 600;
  color: #6b7280;
  margin: 0 0 0.75rem;
  padding: 0 0.25rem;
}
.game-list {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}
.game-pick-item {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  padding: 0.5rem 0.6rem;
  border: none;
  border-radius: 8px;
  background: transparent;
  cursor: pointer;
  text-align: left;
  transition: background 0.15s;
}
.game-pick-item:hover {
  background: #f3f4f6;
}
.game-pick-item.active {
  background: #eff6ff;
  color: #1d4ed8;
}
.game-pick-thumb {
  width: 36px;
  height: 36px;
  border-radius: 6px;
  overflow: hidden;
  background: #e5e7eb;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.game-pick-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.game-pick-thumb .no-img {
  font-size: 0.85rem;
  color: #9ca3af;
}
.game-pick-name {
  font-size: 0.9rem;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.main {
  background: #fff;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
  padding: 1.25rem;
  min-height: 200px;
}
.game-header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #f3f4f6;
}
.game-header-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}
.game-header-thumb {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  overflow: hidden;
  background: #e5e7eb;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.game-header-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.game-header-thumb .no-img {
  font-size: 1.25rem;
  color: #9ca3af;
}
.game-header-name {
  font-size: 1.2rem;
  font-weight: 600;
  margin: 0 0 0.25rem;
  color: #111827;
}
.game-header-meta {
  font-size: 0.85rem;
  color: #6b7280;
  margin: 0;
}
.btn-primary {
  padding: 0.5rem 1rem;
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 0.9rem;
  cursor: pointer;
}
.btn-primary:hover {
  background: #1d4ed8;
}
.records-section {
  margin-top: 0;
}
.records-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 0.75rem;
}
.loading-inline {
  color: #6b7280;
  font-size: 0.9rem;
  padding: 1rem 0;
}
.empty-records {
  color: #6b7280;
  font-size: 0.9rem;
  padding: 1.5rem;
  background: #f9fafb;
  border-radius: 8px;
}
.records-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.record-item {
  padding: 0.75rem 0;
  border-bottom: 1px solid #f3f4f6;
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  justify-content: space-between;
  gap: 0.5rem 1rem;
}
.record-item:last-child {
  border-bottom: none;
}
.record-content {
  flex: 1;
  min-width: 0;
}
.record-actions {
  display: flex;
  gap: 0.35rem;
  flex-shrink: 0;
}
.btn-record {
  padding: 0.25rem 0.5rem;
  font-size: 0.8rem;
  border-radius: 4px;
  border: 1px solid transparent;
  cursor: pointer;
}
.btn-edit {
  background: #eff6ff;
  color: #1d4ed8;
  border-color: #bfdbfe;
}
.btn-edit:hover {
  background: #dbeafe;
}
.btn-delete {
  background: #fef2f2;
  color: #b91c1c;
  border-color: #fecaca;
}
.btn-delete:hover {
  background: #fee2e2;
}
.record-date {
  font-weight: 600;
  color: #111827;
  min-width: 7rem;
}
.record-count {
  font-size: 0.9rem;
  color: #6b7280;
}
.record-memo {
  width: 100%;
  margin: 0.25rem 0 0;
  font-size: 0.9rem;
  color: #4b5563;
  line-height: 1.4;
  white-space: pre-wrap;
}
</style>
