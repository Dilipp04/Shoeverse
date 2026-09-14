import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { ShopProvider } from './context/ShopContext'
import Layout from './components/layout/Layout'
import { AdminRoute, ProtectedRoute } from './components/common/Guards'
import HomePage from './pages/HomePage'
import CatalogPage from './pages/CatalogPage'
import ProductPage from './pages/ProductPage'
import CartPage from './pages/CartPage'
import CheckoutPage from './pages/CheckoutPage'
import AuthPage from './pages/AuthPage'
import AccountPage from './pages/AccountPage'
import AdminPage from './pages/AdminPage'

export default function App() {
  return <BrowserRouter><ShopProvider><Layout><Routes>
    <Route path="/" element={<HomePage />} />
    <Route path="/shop" element={<CatalogPage />} />
    <Route path="/products/:slug" element={<ProductPage />} />
    <Route path="/cart" element={<CartPage />} />
    <Route path="/checkout" element={<ProtectedRoute><CheckoutPage /></ProtectedRoute>} />
    <Route path="/login" element={<AuthPage login />} />
    <Route path="/register" element={<AuthPage />} />
    <Route path="/account" element={<ProtectedRoute><AccountPage /></ProtectedRoute>} />
    <Route path="/admin" element={<AdminRoute><AdminPage /></AdminRoute>} />
    <Route path="*" element={<Navigate to="/" replace />} />
  </Routes></Layout></ShopProvider></BrowserRouter>
}
