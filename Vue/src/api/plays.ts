import client from './client'

/** プレイ記録1件のAPIレスポンス */
export interface PlayRecord {
  id: number
  userBoardGameId: number
  playedAt: string
  memo: string | null
  playerCount: number | null
  createdAt: string
}

/** プレイ記録の追加・更新リクエスト */
export interface PlayRecordRequest {
  playedAt: string
  memo?: string
  playerCount?: number
}

/** Spring Data Page のレスポンス形状 */
export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
  first: boolean
  last: boolean
}

/** プレイ記録の一覧・追加・更新・削除API */
export const playsApi = {
  /** 指定ゲームに紐づくプレイ記録一覧を取得する（ページ形式） */
  listByGame(userBoardGameId: number, params?: { page?: number; size?: number }) {
    return client.get<Page<PlayRecord>>(`/me/boardgames/${userBoardGameId}/plays`, { params })
  },
  /** 全プレイ記録を取得する（ページ形式） */
  listAll(params?: { page?: number; size?: number }) {
    return client.get<Page<PlayRecord>>('/me/plays', { params })
  },
  /** 指定ゲームにプレイ記録を1件追加する */
  add(userBoardGameId: number, data: PlayRecordRequest) {
    return client.post<PlayRecord>(`/me/boardgames/${userBoardGameId}/plays`, data)
  },
  /** 指定プレイ記録を更新する */
  update(userBoardGameId: number, playRecordId: number, data: PlayRecordRequest) {
    return client.put<PlayRecord>(`/me/boardgames/${userBoardGameId}/plays/${playRecordId}`, data)
  },
  /** 指定プレイ記録を削除する */
  delete(userBoardGameId: number, playRecordId: number) {
    return client.delete(`/me/boardgames/${userBoardGameId}/plays/${playRecordId}`)
  },
}
