import client from './client'

export interface WishlistItem {
  id: number
  name: string
  thumbnailUrl: string | null
  addedAt: string
}

export interface AddWishlistItemPayload {
  name: string
  thumbnailUrl?: string
}

export const wishlistApi = {
  list() {
    return client.get<WishlistItem[]>('/me/wishlist')
  },
  add(payload: AddWishlistItemPayload) {
    return client.post<WishlistItem>('/me/wishlist', payload)
  },
  delete(id: number) {
    return client.delete(`/me/wishlist/${id}`)
  },
}
