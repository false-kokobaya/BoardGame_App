import client from './client'

/** ほしいものリスト1件のAPIレスポンス */
export interface WishlistItem {
  id: number
  name: string
  thumbnailUrl: string | null
  addedAt: string
}

/** ほしいものリスト追加のリクエスト */
export interface AddWishlistItemPayload {
  name: string
  thumbnailUrl?: string
}

/** ほしいものリストの一覧・追加・削除API */
export const wishlistApi = {
  /** ほしいもの一覧を取得する */
  list() {
    return client.get<WishlistItem[]>('/me/wishlist')
  },
  /** ほしいものに1件追加する */
  add(payload: AddWishlistItemPayload) {
    return client.post<WishlistItem>('/me/wishlist', payload)
  },
  /** 指定IDのほしいものを削除する */
  delete(id: number) {
    return client.delete(`/me/wishlist/${id}`)
  },
}
