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

/** プレイ記録の一覧・追加・更新・削除API */
export const playsApi = {
  /** 指定ゲームに紐づくプレイ記録一覧を取得する */
  listByGame(userBoardGameId: number) {
    return client.get<PlayRecord[]>(`/me/boardgames/${userBoardGameId}/plays`)
  },
  /** 全プレイ記録を取得する */
  listAll() {
    return client.get<PlayRecord[]>('/me/plays')
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
