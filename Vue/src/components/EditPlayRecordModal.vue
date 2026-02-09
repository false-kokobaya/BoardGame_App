<script setup lang="ts">
import { ref, watch } from 'vue'
import { playsApi, type PlayRecord, type PlayRecordRequest } from '@/api/plays'

const props = defineProps<{
  play: PlayRecord | null
}>()
const emit = defineEmits<{
  close: []
  updated: []
}>()

const playedAt = ref('')
const memo = ref('')
const playerCount = ref<number | ''>('')
const loading = ref(false)
const error = ref('')

watch(() => props.play, (p) => {
  if (!p) return
  playedAt.value = p.playedAt
  memo.value = p.memo ?? ''
  playerCount.value = p.playerCount ?? ''
}, { immediate: true })

async function submit() {
  if (!props.play) return
  if (!playedAt.value) {
    error.value = '日付を入力してください'
    return
  }
  loading.value = true
  error.value = ''
  try {
    const payload: PlayRecordRequest = {
      playedAt: playedAt.value,
      memo: memo.value.trim() || undefined,
      playerCount: playerCount.value === '' ? undefined : Number(playerCount.value),
    }
    await playsApi.update(props.play.userBoardGameId, props.play.id, payload)
    emit('updated')
    emit('close')
  } catch (e: unknown) {
    const err = e as { response?: { data?: { error?: string } } }
    error.value = err.response?.data?.error ?? '更新に失敗しました'
  } finally {
    loading.value = false
  }
}

function close() {
  emit('close')
}
</script>

<template>
  <div v-if="play" class="modal-overlay" @click.self="close">
    <div class="modal">
      <div class="modal-header">
        <h2>プレイ記録を編集</h2>
        <button type="button" class="btn-close" @click="close" aria-label="閉じる">&times;</button>
      </div>
      <form @submit.prevent="submit" class="modal-body">
        <div class="field">
          <label for="edit-playedAt">プレイ日</label>
          <input id="edit-playedAt" v-model="playedAt" type="date" required />
        </div>
        <div class="field">
          <label for="edit-playerCount">プレイ人数（任意）</label>
          <input id="edit-playerCount" v-model.number="playerCount" type="number" min="1" placeholder="例: 4" />
        </div>
        <div class="field">
          <label for="edit-memo">メモ（任意）</label>
          <textarea id="edit-memo" v-model="memo" rows="3" placeholder="メモ"></textarea>
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <div class="actions">
          <button type="button" class="btn-secondary" @click="close">キャンセル</button>
          <button type="submit" class="btn-primary" :disabled="loading">保存</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}
.modal {
  background: #fff;
  border-radius: 8px;
  max-width: 400px;
  width: 100%;
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #e5e7eb;
}
.modal-header h2 {
  font-size: 1.25rem;
}
.btn-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  line-height: 1;
  color: #6b7280;
  padding: 0 0.25rem;
}
.btn-close:hover {
  color: #1f2937;
}
.modal-body {
  padding: 1rem;
}
.field {
  margin-bottom: 1rem;
}
.field label {
  display: block;
  margin-bottom: 0.25rem;
  font-size: 0.9rem;
  color: #555;
}
.field input,
.field textarea {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 4px;
}
.error {
  color: #b91c1c;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}
.actions {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
  margin-top: 1rem;
}
.btn-primary {
  padding: 0.5rem 1rem;
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 4px;
}
.btn-secondary {
  padding: 0.5rem 1rem;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 4px;
}
.btn-primary:disabled {
  opacity: 0.7;
}
</style>
