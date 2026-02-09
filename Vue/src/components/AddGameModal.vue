<script setup lang="ts">
import { ref } from 'vue'
import { boardgamesApi, type AddBoardGamePayload } from '@/api/boardgames'

const emit = defineEmits<{
  close: []
  added: []
}>()

const name = ref('')
const thumbnailUrl = ref('')
const yearPublished = ref<number | ''>('')
const minPlayers = ref<number | ''>('')
const maxPlayers = ref<number | ''>('')
const minPlayTimeMinutes = ref<number | ''>('')
const maxPlayTimeMinutes = ref<number | ''>('')
const loading = ref(false)
const uploadLoading = ref(false)
const error = ref('')
const imageInput = ref<HTMLInputElement | null>(null)

function triggerFileSelect() {
  imageInput.value?.click()
}

async function onImageSelect(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  uploadLoading.value = true
  try {
    const { url } = await boardgamesApi.uploadImage(file)
    thumbnailUrl.value = url
  } catch {
    error.value = '画像のアップロードに失敗しました'
  } finally {
    uploadLoading.value = false
    input.value = ''
  }
}

async function submit() {
  error.value = ''
  if (!name.value.trim()) {
    error.value = 'ゲーム名を入力してください'
    return
  }
  loading.value = true
  try {
    const payload: AddBoardGamePayload = {
      name: name.value.trim(),
      thumbnailUrl: thumbnailUrl.value.trim() || undefined,
      yearPublished: yearPublished.value === '' ? undefined : Number(yearPublished.value),
      minPlayers: minPlayers.value === '' ? undefined : Number(minPlayers.value),
      maxPlayers: maxPlayers.value === '' ? undefined : Number(maxPlayers.value),
      minPlayTimeMinutes: minPlayTimeMinutes.value === '' ? undefined : Number(minPlayTimeMinutes.value),
      maxPlayTimeMinutes: maxPlayTimeMinutes.value === '' ? undefined : Number(maxPlayTimeMinutes.value),
    }
    await boardgamesApi.add(payload)
    emit('added')
    emit('close')
  } catch (e: unknown) {
    const err = e as { response?: { data?: { error?: string } } }
    error.value = err.response?.data?.error ?? '追加に失敗しました'
  } finally {
    loading.value = false
  }
}

function close() {
  emit('close')
}
</script>

<template>
  <div class="modal-overlay" @click.self="close">
    <div class="modal">
      <div class="modal-header">
        <h2>追加</h2>
        <button type="button" class="btn-close" @click="close" aria-label="閉じる">&times;</button>
      </div>
      <form @submit.prevent="submit" class="modal-body">
        <div class="field">
          <label for="name">ゲーム名 <span class="required">*</span></label>
          <input id="name" v-model="name" type="text" required maxlength="500" />
        </div>
        <div class="field">
          <label>画像（任意）</label>
          <div class="image-field">
            <input id="thumbnailUrl" v-model="thumbnailUrl" type="text" placeholder="URLを入力 または 画像を選択" />
            <input
              ref="imageInput"
              type="file"
              accept="image/*"
              class="file-input"
              aria-hidden="true"
              tabindex="-1"
              @change="onImageSelect"
            />
            <button type="button" class="btn-upload" :disabled="uploadLoading" @click="triggerFileSelect">
              {{ uploadLoading ? 'アップロード中...' : '画像を選択' }}
            </button>
          </div>
          <p v-if="thumbnailUrl" class="preview-hint">プレビュー: <img :src="thumbnailUrl" alt="" class="thumb-preview" /></p>
        </div>
        <div class="field">
          <label for="yearPublished">発売年（任意）</label>
          <input id="yearPublished" v-model.number="yearPublished" type="number" min="1900" placeholder="例: 2020" />
        </div>
        <div class="field row">
          <div class="half">
            <label for="minPlayers">最小人数（任意）</label>
            <input id="minPlayers" v-model.number="minPlayers" type="number" min="1" placeholder="1" />
          </div>
          <div class="half">
            <label for="maxPlayers">最大人数（任意）</label>
            <input id="maxPlayers" v-model.number="maxPlayers" type="number" min="1" placeholder="4" />
          </div>
        </div>
        <div class="field row">
          <div class="half">
            <label for="minPlayTime">最短プレイ時間（分・任意）</label>
            <input id="minPlayTime" v-model.number="minPlayTimeMinutes" type="number" min="0" placeholder="30" />
          </div>
          <div class="half">
            <label for="maxPlayTime">最長プレイ時間（分・任意）</label>
            <input id="maxPlayTime" v-model.number="maxPlayTimeMinutes" type="number" min="0" placeholder="60" />
          </div>
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <div class="actions">
          <button type="button" class="btn-secondary" @click="close">キャンセル</button>
          <button type="submit" class="btn-primary" :disabled="loading">追加</button>
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
  max-width: 420px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
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
.field.row {
  display: flex;
  gap: 0.75rem;
}
.half {
  flex: 1;
}
.field label {
  display: block;
  margin-bottom: 0.25rem;
  font-size: 0.9rem;
  color: #374151;
}
.required {
  color: #b91c1c;
}
.field input {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 4px;
}
.image-field {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  align-items: center;
}
.image-field input[type="text"] {
  flex: 1;
  min-width: 0;
}
.file-input {
  position: fixed;
  left: -9999px;
  width: 0;
  height: 0;
  opacity: 0;
  pointer-events: none;
}
.btn-upload {
  padding: 0.5rem 0.75rem;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 0.9rem;
  white-space: nowrap;
}
.btn-upload:disabled {
  opacity: 0.7;
}
.preview-hint {
  margin-top: 0.5rem;
  font-size: 0.85rem;
  color: #6b7280;
}
.thumb-preview {
  display: block;
  margin-top: 0.25rem;
  max-width: 120px;
  max-height: 120px;
  object-fit: contain;
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
