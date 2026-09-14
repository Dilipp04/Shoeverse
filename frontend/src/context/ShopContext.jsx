import { createContext, useContext, useEffect, useState } from 'react'
import { api, clearSession } from '../api'

const ShopContext = createContext(null)

export function ShopProvider({ children }) {
    const [products, setProducts] = useState([])
    const [categories, setCategories] = useState([])
    const [catalogLoading, setCatalogLoading] = useState(true)
    const [catalogError, setCatalogError] = useState('')
    const [cart, setCart] = useState(null)
    const [toast, setToast] = useState('')
    const [user, setUser] = useState(() => JSON.parse(localStorage.getItem('shoeverse_user') || localStorage.getItem('shopverse_user') || 'null'))
    const signed = Boolean(user && (localStorage.getItem('shoeverse_token') || localStorage.getItem('shopverse_token')))

    const reloadCart = async () => {
        if (!localStorage.getItem('shoeverse_token') && !localStorage.getItem('shopverse_token')) return
        try {
            setCart(await api('/api/cart'))
        } catch (error) {
            setToast(error.message)
        }
    }

    useEffect(() => {
        let active = true
        Promise.all([api('/api/products?size=24'), api('/api/categories')])
            .then(([productPage, categoryList]) => {
                if (!active) return
                setProducts(productPage?.content || [])
                setCategories(categoryList || [])
            })
            .catch(error => {
                if (!active) return
                setCatalogError(error.message || 'Unable to load products.')
            })
            .finally(() => { if (active) setCatalogLoading(false) })
        return () => { active = false }
    }, [])

    useEffect(() => { reloadCart() }, [signed])

    const addToCart = async (product, quantity = 1) => {
        if (!signed) {
            setToast('Please sign in before adding items to your bag.')
            return
        }
        try {
            setCart(await api('/api/cart/items', { method: 'POST', body: JSON.stringify({ productId: product.id, quantity }) }))
            setToast(`${product.name} added to your bag.`)
        } catch (error) {
            setToast(error.message)
        }
    }

    const updateCartItem = async (item, quantity) => {
        try {
            const nextCart = quantity < 1
                ? await api(`/api/cart/items/${item.id}`, { method: 'DELETE' })
                : await api(`/api/cart/items/${item.id}`, { method: 'PUT', body: JSON.stringify({ quantity }) })
            setCart(nextCart)
        } catch (error) {
            setToast(error.message)
        }
    }

    const logout = () => {
        clearSession()
        setUser(null)
        setCart(null)
    }

    return <ShopContext.Provider value={{ products, categories, catalogLoading, catalogError, cart, user, signed, setUser, reloadCart, addToCart, updateCartItem, logout, toast, setToast }}>
        {children}
    </ShopContext.Provider>
}

export function useShop() {
    const context = useContext(ShopContext)
    if (!context) throw new Error('useShop must be used inside ShopProvider')
    return context
}