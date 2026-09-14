import { useEffect, useState } from 'react'
import { Edit3, Plus, Trash2, X } from 'lucide-react'
import { api } from '../api'
import { useShop } from '../context/ShopContext'
import { formatPrice } from '../data/catalog'
import { Tab } from '../components/common/Tabs'

const emptyProduct = { name: '', description: '', price: '', discountPrice: '', stockQuantity: '', sku: '', brand: '', categoryId: '', imageUrl: '', active: true }
const productFields = [['name', 'Product name'], ['sku', 'SKU'], ['brand', 'Brand'], ['price', 'Price'], ['discountPrice', 'Discount price'], ['stockQuantity', 'Stock quantity'], ['imageUrl', 'Image URL']]
const statusOptions = ['PLACED', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED']

function labelFor(key) {
    return key.replace(/([A-Z])/g, ' $1').replace(/^./, character => character.toUpperCase())
}

function DashboardList({ title, items, valueKeys, valueFormat = value => String(value) }) {
    return <article className="rounded-2xl bg-white p-6 ring-1 ring-slate-200"><h2 className="font-serif text-2xl">{title}</h2>{items?.length ? <div className="mt-5 space-y-3">{items.slice(0, 6).map((item, index) => <div key={`${title}-${index}`} className="flex items-center justify-between gap-4 border-b border-slate-100 pb-3 text-sm last:border-0 last:pb-0"><span className="text-slate-600">{String(item[valueKeys[0]] ?? item.name ?? item.label ?? `Item ${index + 1}`)}</span><b>{valueKeys[1] && item[valueKeys[1]] != null ? valueFormat(item[valueKeys[1]]) : ''}</b></div>)}</div> : <p className="mt-5 text-sm text-slate-500">No data yet.</p>}</article>
}

export default function AdminPage() {
    const { user, categories, setToast } = useShop()
    const [tab, setTab] = useState('dashboard')
    const [dashboard, setDashboard] = useState(null)
    const [orders, setOrders] = useState([])
    const [products, setProducts] = useState([])
    const [product, setProduct] = useState(emptyProduct)
    const [editingId, setEditingId] = useState(null)
    const [saving, setSaving] = useState(false)
    const isSuperAdmin = user?.role === 'SUPER_ADMIN'

    const loadDashboard = () => api('/api/admin/dashboard').then(setDashboard).catch(error => setToast(error.message))
    const loadOrders = () => api('/api/admin/orders?size=30').then(page => setOrders(page.content || [])).catch(error => setToast(error.message))
    const loadProducts = () => api('/api/products?size=100').then(page => setProducts(page.content || [])).catch(error => setToast(error.message))
    const load = () => { loadDashboard(); loadOrders(); loadProducts() }

    useEffect(() => { load() }, [])

    const updateStatus = async (id, status) => {
        try { await api(`/api/admin/orders/${id}/status?status=${status}`, { method: 'PUT' }); await loadOrders(); setToast('Order status updated.') } catch (error) { setToast(error.message) }
    }

    const updateProductField = (key, value) => setProduct(current => ({ ...current, [key]: value }))
    const editProduct = item => {
        setEditingId(item.id)
        setProduct({ name: item.name || '', description: item.description || '', price: item.price || '', discountPrice: item.discountPrice || '', stockQuantity: item.stockQuantity ?? '', sku: item.sku || '', brand: item.brand || '', categoryId: item.categoryId || '', imageUrl: item.imageUrl || '', active: item.active ?? true })
        setTab('products')
        window.scrollTo({ top: 0, behavior: 'smooth' })
    }
    const resetProduct = () => { setEditingId(null); setProduct(emptyProduct) }
    const saveProduct = async event => {
        event.preventDefault()
        setSaving(true)
        const payload = { ...product, price: Number(product.price), stockQuantity: Number(product.stockQuantity), categoryId: Number(product.categoryId), discountPrice: product.discountPrice === '' ? null : Number(product.discountPrice) }
        try {
            await api(editingId ? `/api/products/${editingId}` : '/api/products', { method: editingId ? 'PUT' : 'POST', body: JSON.stringify(payload) })
            await loadProducts()
            resetProduct()
            setToast(editingId ? 'Product updated successfully.' : 'Product created successfully.')
        } catch (error) { setToast(error.message) } finally { setSaving(false) }
    }
    const deleteProduct = async item => {
        if (!window.confirm(`Delete ${item.name}? This cannot be undone.`)) return
        try { await api(`/api/products/${item.id}`, { method: 'DELETE' }); await loadProducts(); setToast('Product deleted successfully.') } catch (error) { setToast(error.message) }
    }

    const metrics = [['totalSales', 'Total sales', value => formatPrice(value)], ['totalOrders', 'Total orders', value => Number(value).toLocaleString('en-IN')], ['totalCustomers', 'Total customers', value => Number(value).toLocaleString('en-IN')], ['totalProducts', 'Total products', value => Number(value).toLocaleString('en-IN')]]
    return <section className="mx-auto max-w-7xl px-5 py-12 lg:px-12"><p className="text-xs font-bold uppercase tracking-widest text-[#a96f36]">{isSuperAdmin ? 'Super admin' : 'Admin'} workspace</p><h1 className="mt-2 font-serif text-4xl">Store management</h1><div className="mt-9 flex gap-6 overflow-auto border-b border-slate-200"><Tab active={tab === 'dashboard'} onClick={() => setTab('dashboard')}>Dashboard</Tab><Tab active={tab === 'orders'} onClick={() => setTab('orders')}>Orders</Tab><Tab active={tab === 'products'} onClick={() => setTab('products')}>Products</Tab></div>
        {tab === 'dashboard' && <div className="mt-7 space-y-6"><div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">{metrics.map(([key, title, display]) => <article key={key} className="rounded-2xl bg-white p-6 ring-1 ring-slate-200"><p className="text-xs font-bold uppercase tracking-wider text-slate-500">{title}</p><p className="mt-4 font-serif text-4xl">{dashboard?.[key] == null ? '-' : display(dashboard[key])}</p></article>)}</div><div className="grid gap-6 lg:grid-cols-3"><DashboardList title="Sales overview" items={dashboard?.salesOverview} valueKeys={['label', 'value']} /><DashboardList title="Orders overview" items={dashboard?.ordersOverview} valueKeys={['label', 'value']} /><DashboardList title="Revenue by month" items={dashboard?.revenueByMonth} valueKeys={['month', 'revenue']} valueFormat={formatPrice} /></div><DashboardList title="Top products" items={dashboard?.topProducts} valueKeys={['name', 'sales']} /></div>}
        {tab === 'orders' && <div className="mt-7 overflow-x-auto rounded-2xl bg-white ring-1 ring-slate-200"><table className="min-w-full text-left text-sm"><thead className="border-b bg-slate-50 text-xs uppercase text-slate-500"><tr><th className="p-4">Order</th><th className="p-4">Status</th><th className="p-4">Amount</th><th className="p-4">Update</th></tr></thead><tbody>{orders.map(order => <tr key={order.id} className="border-b last:border-0"><td className="p-4 font-medium">{order.orderNumber}</td><td className="p-4">{order.orderStatus}</td><td className="p-4">{formatPrice(order.totalAmount)}</td><td className="p-4"><select value={order.orderStatus} onChange={event => updateStatus(order.id, event.target.value)} className="rounded border px-2 py-1 text-xs">{statusOptions.map(status => <option key={status}>{status}</option>)}</select></td></tr>)}</tbody></table>{!orders.length && <p className="p-8 text-center text-slate-500">No orders found.</p>}</div>}
        {tab === 'products' && <div className="mt-7 grid gap-7 lg:grid-cols-[minmax(0,1fr)_380px]"><div className="overflow-x-auto rounded-2xl bg-white ring-1 ring-slate-200"><table className="min-w-full text-left text-sm"><thead className="border-b bg-slate-50 text-xs uppercase text-slate-500"><tr><th className="p-4">Product</th><th className="p-4">Price</th><th className="p-4">Stock</th><th className="p-4">Actions</th></tr></thead><tbody>{products.map(item => <tr key={item.id} className="border-b last:border-0"><td className="p-4"><b>{item.name}</b><p className="text-xs text-slate-500">{item.brand || item.categoryName} · {item.sku}</p></td><td className="p-4">{formatPrice(item.discountPrice ?? item.price)}</td><td className="p-4">{item.stockQuantity}</td><td className="p-4"><div className="flex gap-2"><button aria-label={`Edit ${item.name}`} title="Edit product" onClick={() => editProduct(item)} className="rounded-lg border p-2 text-slate-600 hover:bg-slate-50"><Edit3 size={16} /></button><button aria-label={`Delete ${item.name}`} title="Delete product" onClick={() => deleteProduct(item)} className="rounded-lg border p-2 text-red-600 hover:bg-red-50"><Trash2 size={16} /></button></div></td></tr>)}</tbody></table>{!products.length && <p className="p-8 text-center text-slate-500">No products found.</p>}</div><form onSubmit={saveProduct} className="h-fit rounded-2xl bg-white p-6 ring-1 ring-slate-200"><div className="flex items-center justify-between"><h2 className="font-serif text-2xl">{editingId ? 'Edit product' : 'Add product'}</h2>{editingId && <button type="button" aria-label="Cancel editing" onClick={resetProduct} className="rounded-full p-1 hover:bg-slate-100"><X size={18} /></button>}</div><div className="mt-5 grid gap-3">{productFields.map(([key, label]) => <input key={key} required={!['discountPrice', 'imageUrl'].includes(key)} type={['price', 'discountPrice', 'stockQuantity'].includes(key) ? 'number' : 'text'} min={['price', 'discountPrice'].includes(key) ? '0.01' : '0'} value={product[key]} onChange={event => updateProductField(key, event.target.value)} placeholder={label} className="rounded-lg border px-3 py-2.5 text-sm" />)}<select required value={product.categoryId} onChange={event => updateProductField('categoryId', event.target.value)} className="rounded-lg border px-3 py-2.5 text-sm"><option value="">Select category</option>{categories.map(category => <option key={category.id} value={category.id}>{category.name}</option>)}</select><textarea required value={product.description} onChange={event => updateProductField('description', event.target.value)} placeholder="Description" className="min-h-28 rounded-lg border px-3 py-2.5 text-sm" /><label className="flex items-center gap-2 text-sm"><input type="checkbox" checked={product.active} onChange={event => updateProductField('active', event.target.checked)} /> Active product</label></div><button disabled={saving} className="mt-5 flex w-full items-center justify-center gap-2 rounded-lg bg-[#101726] px-6 py-3 text-sm font-bold text-white disabled:opacity-60">{editingId ? <Edit3 size={16} /> : <Plus size={16} />}{saving ? 'Saving...' : editingId ? 'Update product' : 'Create product'}</button></form></div>}
    </section>
}
