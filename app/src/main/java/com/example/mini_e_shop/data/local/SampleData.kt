package com.example.mini_e_shop.data.local

import com.example.mini_e_shop.data.local.entity.ProductEntity

object SampleData {
    fun getSampleProducts(): List<ProductEntity> {
        return listOf(
            ProductEntity(
                id = 0,
                name = "iPhone 14 Pro", brand = "Apple", category = "Điện thoại",
                origin = "USA", price = 999.0, stock = 50,
                imageUrl = "https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/iphone-14-pro-model-unselect-gallery-2-202209_GEO_US?wid=5120&hei=2880&fmt=p-jpg&qlt=80&.v=1660753617560",
                description = "iPhone 14 Pro with Dynamic Island."
            ),
            ProductEntity(
                id = 0,
                name = "Samsung Galaxy S23 Ultra", brand = "Samsung", category = "Điện thoại",
                origin = "Korea", price = 1199.0, stock = 40,
                imageUrl = "https://images.samsung.com/is/image/samsung/p6pim/vn/2302/gallery/vn-galaxy-s23-ultra-s918-sm-s918bzevxxv-534860493?\$650_519_PNG\$",
                description = "The ultimate Galaxy phone with a built-in S Pen."
            ),
            ProductEntity(
                id = 0,
                name = "MacBook Air M2", brand = "Apple", category = "Laptop",
                origin = "USA", price = 1199.0, stock = 30,
                imageUrl = "https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/macbook-air-midnight-select-20220606?wid=904&hei=840&fmt=jpeg&qlt=90&.v=1652836879941",
                description = "Supercharged by the M2 chip."
            ),
            ProductEntity(
                id = 0,
                name = "Dell XPS 15", brand = "Dell", category = "Laptop",
                origin = "USA", price = 1499.0, stock = 25,
                imageUrl = "https://i.dell.com/is/image/DellContent/content/dam/ss2/product-images/dell-client-products/notebooks/xps-notebooks/xps-15-9530/media-gallery/black/notebook-xps-15-9530-nt-black-gallery-3.psd?fmt=pjpg&pscan=auto&scl=1&hei=402&wid=677&qlt=100,0&resMode=sharp2&size=677,402&chrss=full",
                description = "Stunning display and powerful performance."
            ),
            ProductEntity(
                id = 0,
                name = "Sony WH-1000XM5", brand = "Sony", category = "Tai nghe",
                origin = "Japan", price = 399.0, stock = 60,
                imageUrl = "https://www.sony.com.vn/image/5d02da5df552836db894cead8a68f1f3?fmt=pjpeg&wid=330&bgcolor=FFFFFF&bgc=FFFFFF",
                description = "Industry-leading noise canceling headphones."
            ),
            ProductEntity(
                id = 0,
                name = "Apple Watch Series 8", brand = "Apple", category = "Đồng hồ",
                origin = "USA", price = 399.0, stock = 70,
                imageUrl = "https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/MP6V3_VW_34FR+watch-41-alum-midnight-nc-8s_VW_34FR_WF_CO?wid=752&hei=720&fmt=p-jpg&qlt=80&.v=1661471783108",
                description = "A healthy leap ahead."
            ),
            ProductEntity(
                id = 0,
                name = "Logitech MX Master 3S", brand = "Logitech", category = "Phụ kiện",
                origin = "Switzerland", price = 99.0, stock = 100,
                imageUrl = "https://resource.logitech.com/w_800,c_lpad,ar_1:1,q_auto,f_auto,dpr_1.0/d_transparent.gif/content/dam/logitech/en/products/mice/mx-master-3s/gallery/mx-master-3s-mouse-top-view-graphite.png?v=1",
                description = "Iconic mouse, remastered."
            ),
            ProductEntity(
                id = 0,
                name = "Canon EOS R6 Mark II", brand = "Canon", category = "Máy ảnh",
                origin = "Japan", price = 2499.0, stock = 15,
                imageUrl = "https://i1.adis.ws/i/canon/eos-r6-mark-ii_800x800_square_32c19186634a491897b39c05e13a96a3?\$prod-gallery-1by1-jpg\$",
                description = "A hybrid powerhouse for stills and video."
            ),
            ProductEntity(
                id = 0,
                name = "Nintendo Switch OLED", brand = "Nintendo", category = "Gaming",
                origin = "Japan", price = 349.0, stock = 45,
                imageUrl = "https://assets.nintendo.com/image/upload/f_auto,q_auto,w_960,h_540/ncom/en_US/switch/site-design/oled-model-promo",
                description = "Play at home or on the go."
            ),
            ProductEntity(
                id = 0,
                name = "GoPro HERO11 Black", brand = "GoPro", category = "Máy ảnh",
                origin = "USA", price = 499.0, stock = 35,
                imageUrl = "https://cdn.gopro.com/assets/blta2b8522e5372af59/blt51412a873138ed22/631e13e9a7448c7c7333a25e/pdp-h11-black-dual-l.png?width=750&quality=85&auto=webp&disable=upscale",
                description = "The most versatile GoPro yet."
            ),
            ProductEntity(
                id = 0,
                name = "iPad Pro M2", brand = "Apple", category = "Tablet",
                origin = "USA", price = 799.0, stock = 30,
                imageUrl = "https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/ipad-pro-11-select-wifi-spacegray-202210?wid=940&hei=1112&fmt=p-jpg&qlt=95&.v=1664411231432",
                description = "The ultimate iPad experience."
            ),
            ProductEntity(
                id = 0,
                name = "Samsung Odyssey G9", brand = "Samsung", category = "Màn hình",
                origin = "Korea", price = 1399.0, stock = 20,
                imageUrl = "https://images.samsung.com/is/image/samsung/p6pim/vn/ls49cg932sexxv/gallery/vn-odyssey-oled-g9-g93sc-ls49cg932sexxv-537452654?\$650_519_PNG\$",
                description = "49-inch curved gaming monitor."
            ),
            ProductEntity(
                id = 0,
                name = "AirPods Pro (2nd gen)", brand = "Apple", category = "Tai nghe",
                origin = "USA", price = 249.0, stock = 80,
                imageUrl = "https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/MQD83?wid=1144&hei=1144&fmt=jpeg&qlt=90&.v=1660803972361",
                description = "Rebuilt for more immersive sound."
            ),
            ProductEntity(
                id = 0,
                name = "Razer BlackWidow V4 Pro", brand = "Razer", category = "Bàn phím",
                origin = "USA", price = 229.0, stock = 50,
                imageUrl = "https://assets2.razerzone.com/images/pnx.assets/d21b099b647f3b827357c91185121b6d/razer-blackwidow-v4-pro-500x500.png",
                description = "A mechanical gaming keyboard."
            ),
            ProductEntity(
                id = 0,
                name = "Anker 737 Power Bank", brand = "Anker", category = "Phụ kiện",
                origin = "China", price = 149.0, stock = 90,
                imageUrl = "https://m.media-amazon.com/images/I/71u36oNfV3L._AC_SL1500_.jpg",
                description = "24,000mAh 140W portable charger."
            ),
            ProductEntity(
                id = 0,
                name = "Google Pixel 7 Pro", brand = "Google", category = "Điện thoại",
                origin = "USA", price = 899.0, stock = 40,
                imageUrl = "https://storage.googleapis.com/gweb-uniblog-publish-prod/original_images/Pixel_7_Pro_Hazel_1.jpg",
                description = "The all-pro Google phone."
            ),
            ProductEntity(
                id = 0,
                name = "Bose QuietComfort Earbuds II", brand = "Bose", category = "Tai nghe",
                origin = "USA", price = 299.0, stock = 55,
                imageUrl = "https://assets.bose.com/content/dam/Bose_DAM/Web/consumer_electronics/global/products/headphones/qc_earbuds_II/product_silo_images/QCEBII_product-silo_TripleBlack_1200x1023.png/jcr:content/renditions/cq5dam.web.320.320.png",
                description = "The world's best noise cancellation."
            ),
            ProductEntity(
                id = 0,
                name = "Kindle Paperwhite", brand = "Amazon", category = "Tablet",
                origin = "USA", price = 139.0, stock = 120,
                imageUrl = "https://m.media-amazon.com/images/I/61-262zL0gL._AC_SL1000_.jpg",
                description = "Read anytime, anywhere."
            ),
            ProductEntity(
                id = 0,
                name = "Elgato Stream Deck MK.2", brand = "Elgato", category = "Phụ kiện",
                origin = "Germany", price = 149.0, stock = 60,
                imageUrl = "https://m.media-amazon.com/images/I/71g3R+tva2L._AC_SL1500_.jpg",
                description = "15 customizable LCD keys for streamers."
            ),
            ProductEntity(
                id = 0,
                name = "Sony a7 IV", brand = "Sony", category = "Máy ảnh",
                origin = "Japan", price = 2498.0, stock = 20,
                imageUrl = "https://www.sony.com.vn/image/5d02da5df552836db894cead8a68f1f3?fmt=pjpeg&wid=330&bgcolor=FFFFFF&bgc=FFFFFF",
                description = "A7 IV: Beyond basic."
            )
        )
    }
}