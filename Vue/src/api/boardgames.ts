import client from './client'

/** ボードゲーム1件のAPIレスポンス */
export interface UserBoardGame {
  id: number
  bggId: string | null
  name: string
  thumbnailUrl: string | null
  yearPublished: number | null
  minPlayers: number | null
  maxPlayers: number | null
  minPlayTimeMinutes: number | null
  maxPlayTimeMinutes: number | null
  addedAt: string
}

/** ボードゲーム追加のリクエスト */
export interface AddBoardGamePayload {
  name: string
  thumbnailUrl?: string
  yearPublished?: number
  minPlayers?: number
  maxPlayers?: number
  minPlayTimeMinutes?: number
  maxPlayTimeMinutes?: number
}

/** ボードゲーム更新のリクエスト */
export interface UpdateBoardGamePayload {
  name?: string
  thumbnailUrl?: string
  yearPublished?: number
  minPlayers?: number
  maxPlayers?: number
  minPlayTimeMinutes?: number
  maxPlayTimeMinutes?: number
}

/** 画像アップロードのレスポンス（表示用URL） */
export interface UploadImageResponse {
  url: string
}

/** マイページのボードゲーム一覧・追加・更新・削除・画像アップロードAPI */
export const boardgamesApi = {
  /** 所持ゲーム一覧を取得する */
  list() {
    return client.get<UserBoardGame[]>('/me/boardgames')
  },
  /** ゲームを1件追加する */
  add(payload: AddBoardGamePayload) {
    return client.post<UserBoardGame>('/me/boardgames', payload)
  },
  /** 指定IDのゲームを更新する */
  update(id: number, payload: UpdateBoardGamePayload) {
    return client.put<UserBoardGame>(`/me/boardgames/${id}`, payload)
  },
  /** 指定IDのゲームを削除する */
  delete(id: number) {
    return client.delete(`/me/boardgames/${id}`)
  },
  /** 画像ファイルをアップロードし、表示用URLを返す（list/add/update/delete と同様に AxiosResponse を返す） */
  uploadImage(file: File) {
    const form = new FormData()
    form.append('file', file)
    return client.post<UploadImageResponse>('/me/upload-image', form)
  },
}
