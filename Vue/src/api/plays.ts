import client from './client'

export interface PlayRecord {
  id: number
  userBoardGameId: number
  playedAt: string
  memo: string | null
  playerCount: number | null
  createdAt: string
}

export interface PlayRecordRequest {
  playedAt: string
  memo?: string
  playerCount?: number
}

export const playsApi = {
  listByGame(userBoardGameId: number) {
    return client.get<PlayRecord[]>(`/me/boardgames/${userBoardGameId}/plays`)
  },
  listAll() {
    return client.get<PlayRecord[]>('/me/plays')
  },
  add(userBoardGameId: number, data: PlayRecordRequest) {
    return client.post<PlayRecord>(`/me/boardgames/${userBoardGameId}/plays`, data)
  },
  update(userBoardGameId: number, playRecordId: number, data: PlayRecordRequest) {
    return client.put<PlayRecord>(`/me/boardgames/${userBoardGameId}/plays/${playRecordId}`, data)
  },
  delete(userBoardGameId: number, playRecordId: number) {
    return client.delete(`/me/boardgames/${userBoardGameId}/plays/${playRecordId}`)
  },
}
