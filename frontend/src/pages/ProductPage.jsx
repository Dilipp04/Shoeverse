import { ArrowLeft, Minus, Plus, ShoppingBag } from "lucide-react";
import { useEffect, useState } from "react";
import { Link, Navigate, useParams } from "react-router-dom";
import { api } from "../api";
import { formatPrice, getFinalPrice, getProductImage } from "../data/catalog";
import { useShop } from "../context/ShopContext";

export default function ProductPage() {
    const { slug } = useParams();
    const { products, addToCart } = useShop();
    const [product, setProduct] = useState(() =>
        products.find((item) => item.slug === slug || String(item.id) === slug),
    );
    const [loading, setLoading] = useState(!product);
    const [quantity, setQuantity] = useState(1);
    useEffect(() => {
        setLoading(true);
        api(`/api/products/slug/${slug}`)
            .then(setProduct)
            .catch(() => setProduct(null))
            .finally(() => setLoading(false));
    }, [slug]);
    if (loading)
        return (
            <section className="grid min-h-[55vh] place-items-center text-slate-500">
                Loading shoe...
            </section>
        );
    if (!product) return <Navigate to="/shop" replace />;
    return (
        <section className="mx-auto grid max-w-7xl gap-10 px-5 py-12 lg:grid-cols-2 lg:px-12">
            <div className="aspect-square overflow-hidden rounded-4xl bg-[#eee9e2]">
                {getProductImage(product) ? (
                    <img
                        className="h-full w-full object-cover"
                        src={getProductImage(product)}
                        alt={product.name}
                    />
                ) : (
                    <span className="grid h-full place-items-center text-slate-500">
                        Image unavailable
                    </span>
                )}
            </div>
            <div>
                <Link
                    to="/shop"
                    className="flex items-center gap-1 text-sm text-slate-500">
                    <ArrowLeft size={15} /> Back to shop
                </Link>
                <p className="mt-8 text-xs font-bold tracking-widest text-[#a96f36]">
                    {product.brand || product.categoryName}
                </p>
                <h1 className="mt-2 font-serif text-5xl">{product.name}</h1>
                <p className="mt-4 text-2xl font-semibold">
                    {formatPrice(getFinalPrice(product))}
                </p>
                <p className="my-7 border-y border-slate-200 py-6 leading-7 text-slate-600">
                    {product.description}
                </p>
                <p className="text-sm font-semibold text-emerald-700">
                    {product.stockQuantity === 0
                        ? "Out of stock"
                        : "In stock and ready to dispatch"}
                </p>
                <div className="mt-6 flex gap-3">
                    <div className="flex items-center rounded-xl border">
                        <button
                            className="p-3"
                            onClick={() => setQuantity(Math.max(1, quantity - 1))}>
                            <Minus size={16} />
                        </button>
                        <span>{quantity}</span>
                        <button className="p-3" onClick={() => setQuantity(quantity + 1)}>
                            <Plus size={16} />
                        </button>
                    </div>
                    <button
                        disabled={product.stockQuantity === 0}
                        onClick={() => addToCart(product, quantity)}
                        className="flex flex-1 justify-center gap-2 rounded-xl bg-[#101726] py-3.5 text-sm font-bold text-white">
                        <ShoppingBag size={17} /> Add to bag
                    </button>
                </div>
            </div>
        </section>
    );
}
