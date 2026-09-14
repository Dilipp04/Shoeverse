import { ShoppingBag } from 'lucide-react'
import { Link } from 'react-router-dom'

export default function EmptyState({ title, text, to, action }) {
    return <section className="grid min-h-[55vh] place-items-center p-5 text-center">
        <div><ShoppingBag className="mx-auto mb-4 text-[#a96f36]" size={38} /><h1 className="font-serif text-4xl">{title}</h1><p className="mt-3 text-slate-500">{text}</p><Link to={to} className="mt-7 inline-block rounded-xl bg-[#101726] px-6 py-3 text-sm font-semibold text-white">{action}</Link></div>
    </section>
}