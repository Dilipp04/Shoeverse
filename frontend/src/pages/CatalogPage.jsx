import { ArrowRight, Search } from 'lucide-react'
import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { useShop } from '../context/ShopContext'
import ProductCard from '../components/catalog/ProductCard'

export default function CatalogPage() {
    const { products, categories, catalogLoading, catalogError, addToCart } = useShop()
    const [searchParams] = useSearchParams()
    const [query, setQuery] = useState('')
    const view = searchParams.get('view') || 'shop'
    const categoryParam = searchParams.get('category') || 'All'
    const [category, setCategory] = useState(categoryParam)
    const [selectedView, setSelectedView] = useState(view)
    useEffect(() => { setSelectedView(view); setCategory(categoryParam); setQuery('') }, [view, categoryParam])
    const tags = [...new Set([...categories.map(item => item.name), ...products.map(item => item.categoryName)])].filter(Boolean)
    const visibleProducts = products
        .filter(product => `${product.name} ${product.brand || ''}`.toLowerCase().includes(query.toLowerCase()) && (category === 'All' || product.categoryName === category))
        .slice()
        .sort((first, second) => selectedView === 'new' ? new Date(second.createdAt || 0) - new Date(first.createdAt || 0) : 0)
    const tabs = [['shop', 'Shop'], ['new', 'New arrivals'], ['collections', 'Collections']]
    return <section className="mx-auto max-w-7xl px-5 py-12 lg:px-12"><p className="text-xs font-bold uppercase tracking-[.18em] text-[#a96f36]">{selectedView === 'new' ? 'Just landed' : selectedView === 'collections' ? 'Shop by collection' : 'The shoe collection'}</p><h1 className="mt-2 font-serif text-5xl">{selectedView === 'new' ? 'New shoes, fresh energy.' : selectedView === 'collections' ? 'Find your fit.' : 'Find your next pair.'}</h1><div className="mt-8 flex gap-2 overflow-auto border-b border-slate-200 pb-3">{tabs.map(([value, label]) => <Link key={value} to={value === 'shop' ? '/shop' : `/shop?view=${value}`} className={`whitespace-nowrap border-b-2 px-1 pb-3 text-sm font-semibold ${selectedView === value ? 'border-[#a96f36] text-[#a96f36]' : 'border-transparent text-slate-500'}`}>{label}</Link>)}</div>{catalogLoading && <p className="mt-8 text-slate-500">Loading shoes...</p>}{catalogError && <p className="mt-8 rounded-xl bg-red-50 p-4 text-sm text-red-700">{catalogError}</p>}{!catalogLoading && !catalogError && selectedView === 'collections' && <div className="mt-8 grid gap-5 sm:grid-cols-2 lg:grid-cols-4">{categories.map(item => <Link key={item.id} to={`/shop?category=${encodeURIComponent(item.name)}`} className="group overflow-hidden rounded-2xl bg-white ring-1 ring-slate-200"><div className="aspect-[1.25] overflow-hidden bg-[#eee9e2]">{item.imageUrl && <img src={item.imageUrl} alt={item.name} className="h-full w-full object-cover transition duration-500 group-hover:scale-105" />}</div><div className="flex items-center justify-between p-5"><div><h2 className="font-serif text-2xl">{item.name}</h2><p className="mt-1 text-sm text-slate-500">{products.filter(product => product.categoryName === item.name).length} styles</p></div><ArrowRight size={18} className="text-[#a96f36]" /></div></Link>)}</div>}{!catalogLoading && !catalogError && selectedView !== 'collections' && <><label className="mt-8 flex max-w-md items-center rounded-xl bg-white px-4 py-3 ring-1 ring-slate-200"><Search size={18} /><input value={query} onChange={event => setQuery(event.target.value)} className="w-full bg-transparent px-2 outline-none" placeholder="Search shoes" /></label><div className="my-8 flex gap-2 overflow-auto">{['All', ...tags].map(tag => <button key={tag} onClick={() => setCategory(tag)} className={`whitespace-nowrap rounded-full px-4 py-2 text-sm ${category === tag ? 'bg-[#101726] text-white' : 'bg-white ring-1 ring-slate-200'}`}>{tag}</button>)}</div><div className="grid grid-cols-2 gap-5 md:grid-cols-3 lg:grid-cols-4">{visibleProducts.map(product => <ProductCard key={product.id} product={product} onAdd={addToCart} />)}</div>{!visibleProducts.length && <p className="py-16 text-center text-slate-500">No shoes match your search.</p>}</>}</section>
}