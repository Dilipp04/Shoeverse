export function Tab({ active, onClick, children }) {
    return <button onClick={onClick} className={`border-b-2 pb-3 text-sm font-semibold ${active ? 'border-[#a96f36] text-[#a96f36]' : 'border-transparent text-slate-500'}`}>{children}</button>
}

export function Blank({ text }) {
    return <p className="rounded-2xl bg-white p-8 text-center text-slate-500 ring-1 ring-slate-200">{text}</p>
}