import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api, saveSession } from '../api'
import { useShop } from '../context/ShopContext'

export default function AuthPage({ login = false }) {
    const { setUser, setToast } = useShop()
    const navigate = useNavigate()
    const [form, setForm] = useState({ name: '', phone: '', email: '', password: '' })
    const update = (key, value) => setForm(current => ({ ...current, [key]: value }))
    const submit = async event => { event.preventDefault(); try { const auth = await api(`/api/auth/${login ? 'login' : 'register'}`, { method: 'POST', body: JSON.stringify(login ? { email: form.email, password: form.password } : form) }); saveSession(auth); setUser(auth.user); navigate('/shop') } catch (error) { setToast(error.message) } }
    const field = (key, label, type = 'text') => <input required value={form[key]} onChange={event => update(key, event.target.value)} type={type} placeholder={label} className="w-full rounded-xl border px-4 py-3" />
    return <section className="grid min-h-[60vh] place-items-center p-5"><form onSubmit={submit} className="w-full max-w-md rounded-2xl bg-white p-8 ring-1 ring-slate-200"><p className="text-xs font-bold uppercase tracking-widest text-[#a96f36]">{login ? 'Welcome back' : 'Join ShoeVerse'}</p><h1 className="mt-2 font-serif text-4xl">{login ? 'Sign in to continue.' : 'Create your account.'}</h1><div className="mt-7 space-y-4">{!login && <>{field('name', 'Full name')}{field('phone', 'Phone', 'tel')}</>}{field('email', 'Email address', 'email')}{field('password', 'Password', 'password')}</div><button className="mt-7 w-full rounded-xl bg-[#101726] py-3.5 text-sm font-bold text-white">{login ? 'Sign in' : 'Create account'}</button><p className="mt-5 text-center text-sm">{login ? 'New here?' : 'Already have an account?'} <Link className="text-[#a96f36]" to={login ? '/register' : '/login'}>{login ? 'Create account' : 'Sign in'}</Link></p></form></section>
}