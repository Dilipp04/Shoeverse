export const formatPrice = (value) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(value || 0);

export const getFinalPrice = (product) =>
  Number(product.discountPrice ?? product.price ?? 0);
export const getProductImage = (product) => product?.imageUrl;
