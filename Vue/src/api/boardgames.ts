import client from './client'

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

export interface AddBoardGamePayload {
  name: string
  thumbnailUrl?: string
  yearPublished?: number
  minPlayers?: number
  maxPlayers?: number
  minPlayTimeMinutes?: number
  maxPlayTimeMinutes?: number
}

export interface UpdateBoardGamePayload {
  name?: string
  thumbnailUrl?: string
  yearPublished?: number
  minPlayers?: number
  maxPlayers?: number
  minPlayTimeMinutes?: number
  maxPlayTimeMinutes?: number
}

export interface UploadImageResponse {
  url: string
}

export const boardgamesApi = {
  list() {
    return client.get<UserBoardGame[]>('/me/boardgames')
  },
  add(payload: AddBoardGamePayload) {
    return client.post<UserBoardGame>('/me/boardgames', payload)
  },
  update(id: number, payload: UpdateBoardGamePayload) {
    return client.put<UserBoardGame>(`/me/boardgames/${id}`, payload)
  },
  delete(id: number) {
    return client.delete(`/me/boardgames/${id}`)
  },
  /** 画像ファイルをアップロードし、表示用URLを返す */
  async uploadImage(file: File): Promise<UploadImageResponse> {
    const form = new FormData()
    form.append('file', file)
    const { data } = await client.post<UploadImageResponse>('/me/upload-image', form)
    return data
  },
}
