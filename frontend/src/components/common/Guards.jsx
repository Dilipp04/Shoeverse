import { Navigate } from 'react-router-dom'
import { useShop } from '../../context/ShopContext'

export function ProtectedRoute({ children }) {
    return useShop().signed ? children : <Navigate to="/login" replace />
}

export function AdminRoute({ children }) {
    return ['ADMIN', 'SUPER_ADMIN'].includes(useShop().user?.role) ? children : <Navigate to="/account" replace />
}