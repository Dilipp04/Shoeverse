import { Minus, Plus } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { useShop } from '../context/ShopContext'
import { formatPrice, getProductImage } from '../data/catalog'
import EmptyState from '../components/common/EmptyState'

function Summary({ cart }) { return <div className="mt-6 space-y-3 border-y py-5 text-sm"><p className="flex justify-between"><span>Subtotal</span><span>{formatPrice(cart.subtotal)}</span></p><p className="flex justify-between"><span>Delivery</span><span>{Number(cart.shippingCharge) ? formatPrice(cart.shippingCharge) : 'Free'}</span></p><p className="flex justify-between pt-3 text-base font-bold"><span>Total</span><span>{formatPrice(cart.total)}</span></p></div> }

export default function CartPage() {
    const { cart, signed, updateCartItem } = useShop()
    const navigate = useNavigate()
    if (!signed) return <EmptyState title="Your bag is waiting." text="Sign in to save products and checkout securely." to="/login" action="Sign in" />
    if (!cart?.items?.length) return <EmptyState title="Your bag is empty." text="Add something special to get started." to="/shop" action="Explore collection" />
    return <section className="mx-auto max-w-6xl px-5 py-12 lg:px-12"><h1 className="font-serif text-4xl">Your bag</h1><div className="mt-8 grid gap-10 lg:grid-cols-[1fr_360px]"><div>{cart.items.map(item => <div key={item.id} className="flex gap-4 border-b py-5"><div className="h-28 w-24 overflow-hidden rounded-xl bg-[#eee9e2]">{getProductImage(item) && <img className="h-full w-full object-cover" src={getProductImage(item)} alt={item.productName} />}</div><div className="flex flex-1 flex-col"><div className="flex justify-between"><div><p className="font-medium">{item.productName}</p><p className="text-sm text-slate-500">{formatPrice(item.price)} each</p></div><b>{formatPrice(item.subtotal)}</b></div><div className="mt-auto flex justify-between"><div className="flex items-center gap-3 rounded-full border px-2 py-1"><button aria-label="Decrease quantity" onClick={() => updateCartItem(item, item.quantity - 1)}><Minus size={14} /></button><span>{item.quantity}</span><button aria-label="Increase quantity" onClick={() => updateCartItem(item, item.quantity + 1)}><Plus size={14} /></button></div><button onClick={() => updateCartItem(item, 0)} className="text-xs underline">Remove</button></div></div></div>)}</div><aside className="h-fit rounded-2xl bg-white p-6 ring-1 ring-slate-200"><h2 className="font-serif text-2xl">Order summary</h2><Summary cart={cart} /><button onClick={() => navigate('/checkout')} className="mt-6 w-full rounded-xl bg-[#101726] py-4 text-sm font-bold text-white">Checkout securely</button></aside></div></section>
}