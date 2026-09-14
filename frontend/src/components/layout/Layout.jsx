import Header from "./Header";
import { useShop } from "../../context/ShopContext";

export default function Layout({ children }) {
    const { toast, setToast } = useShop();
    return (
        <div className="min-h-screen bg-[#fbfaf7] text-[#101726]">

            <Header />
            {children}
            {toast && (
                <button
                    onClick={() => setToast("")}
                    className="fixed bottom-5 left-1/2 z-50 -translate-x-1/2 rounded-full bg-[#101726] px-5 py-3 text-sm text-white shadow-xl">
                    {toast}
                </button>
            )}
            <footer className="mt-16 bg-[#101726] px-6 py-10 text-slate-300">
                <div className="mx-auto flex max-w-7xl justify-between">
                    <span className="font-serif text-2xl text-white">
                        shoeverse<span className="text-[#e6ba79]">.</span>
                    </span>
                    <span className="text-sm">Everyday, elevated.</span>
                </div>
            </footer>
        </div>
    );
}
