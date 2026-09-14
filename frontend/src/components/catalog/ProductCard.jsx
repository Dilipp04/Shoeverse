import { Plus, Star } from 'lucide-react'
import { Link } from 'react-router-dom'
import { formatPrice, getFinalPrice, getProductImage } from '../../data/catalog'

export default function ProductCard({ product, onAdd }) {
    const path = `/products/${product.slug || product.id}`
    return <article><Link to={path} className="block aspect-[.83] overflow-hidden rounded-2xl bg-[#eee9e2]">{getProductImage(product) ? <img src={getProductImage(product)} className="h-full w-full object-cover transition hover:scale-105" alt={product.name} /> : <span className="grid h-full place-items-center text-sm text-slate-500">Image unavailable</span>}</Link><p className="mt-4 text-[11px] font-bold tracking-widest text-[#a96f36]">{product.brand || product.categoryName}</p><div className="mt-1 flex justify-between gap-2"><Link className="font-medium" to={path}>{product.name}</Link><b className="text-sm">{formatPrice(getFinalPrice(product))}</b></div><div className="mt-3 flex justify-between"><span className="flex items-center gap-1 text-xs text-slate-500"><Star size={13} fill="#ca8b4b" strokeWidth={0} className="text-[#ca8b4b]" />{product.rating || 0}</span><button aria-label={`Add ${product.name} to bag`} onClick={() => onAdd(product)} className="rounded-full border p-1.5"><Plus size={15} /></button></div></article>
}