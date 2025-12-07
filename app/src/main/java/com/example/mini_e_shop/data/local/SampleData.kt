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
                imageUrl = "https://www.androidauthority.com/wp-content/uploads/2023/02/samsung-galaxy-s23-ultra-hero.jpg",
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
                imageUrl = "https://tse2.mm.bing.net/th/id/OIP.W818awHtE8yQN_zufAyJjAHaFj?cb=ucfimg2&ucfimg=1&rs=1&pid=ImgDetMain&o=7&rm=3",
                description = "Stunning display and powerful performance."
            ),
            ProductEntity(
                id = 0,
                name = "Sony WH-1000XM5", brand = "Sony", category = "Tai nghe",
                origin = "Japan", price = 399.0, stock = 60,
                imageUrl = "https://www.bhphotovideo.com/images/images2000x2000/sony_wh1000xm5_s_wh_1000xm5_noise_canceling_wireless_over_ear_1706394.jpg",
                description = "Industry-leading noise canceling headphones."
            ),
            ProductEntity(
                id = 0,
                name = "Apple Watch Series 8", brand = "Apple", category = "Đồng hồ",
                origin = "USA", price = 399.0, stock = 70,
                imageUrl = "https://tse4.mm.bing.net/th/id/OIP.1L2PhweBpQgIgVA45n_G7QHaG8?cb=ucfimg2&ucfimg=1&rs=1&pid=ImgDetMain&o=7&rm=3",
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
                imageUrl = "https://www.cined.com/content/uploads/2022/11/Canon-R6-Mark-II-review.jpg",
                description = "A hybrid powerhouse for stills and video."
            ),
            ProductEntity(
                id = 0,
                name = "Nintendo Switch OLED", brand = "Nintendo", category = "Gaming",
                origin = "Japan", price = 349.0, stock = 45,
                imageUrl = "https://tse4.mm.bing.net/th/id/OIP.ePWj5sQmYNRUsKG_lEqsbAHaFD?cb=ucfimg2&ucfimg=1&rs=1&pid=ImgDetMain&o=7&rm=3",
                description = "Play at home or on the go."
            ),
            ProductEntity(
                id = 0,
                name = "GoPro HERO11 Black", brand = "GoPro", category = "Máy ảnh",
                origin = "USA", price = 499.0, stock = 35,
                imageUrl = "https://tse3.mm.bing.net/th/id/OIP.4UqP0zNbNPA_PJ0yToAw9gHaFW?cb=ucfimg2&ucfimg=1&rs=1&pid=ImgDetMain&o=7&rm=3",
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
                imageUrl = "https://i0.wp.com/9to5toys.com/wp-content/uploads/sites/5/2023/06/Samsung-Odyssey-OLED-G9-lead.jpg?resize=1200%2C628&ssl=1",
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
                imageUrl = "https://mlpnk72yciwc.i.optimole.com/cqhiHLc.IIZS~2ef73/w:auto/h:auto/q:75/https://bleedingcool.com/wp-content/uploads/2023/02/Razer-BlackWidow-V4-Pro-Gaming.jpg",
                description = "A mechanical gaming keyboard."
            ),
            ProductEntity(
                id = 0,
                name = "Anker 737 Power Bank", brand = "Anker", category = "Phụ kiện",
                origin = "China", price = 149.0, stock = 90,
                imageUrl = "https://cdn.shopify.com/s/files/1/0493/9834/9974/products/A1289011-Anker_737_Power_Bank_PowerCore_24K_1_3840x.png?v=1672388225",
                description = "24,000mAh 140W portable charger."
            ),
            ProductEntity(
                id = 0,
                name = "Google Pixel 7 Pro", brand = "Google", category = "Điện thoại",
                origin = "USA", price = 899.0, stock = 40,
                imageUrl = "https://tse3.mm.bing.net/th/id/OIP.j5RlQwxWOyQq_mD7UPg3PAHaFj?cb=ucfimg2&ucfimg=1&rs=1&pid=ImgDetMain&o=7&rm=3",
                description = "The all-pro Google phone."
            ),
            ProductEntity(
                id = 0,
                name = "Bose QuietComfort Earbuds II", brand = "Bose", category = "Tai nghe",
                origin = "USA", price = 299.0, stock = 55,
                imageUrl = "https://tse1.explicit.bing.net/th/id/OIP.4HqGINcybokOQOmOd58kVgHaE7?cb=ucfimg2&ucfimg=1&rs=1&pid=ImgDetMain&o=7&rm=3",
                description = "The world's best noise cancellation."
            ),
            ProductEntity(
                id = 0,
                name = "Kindle Paperwhite", brand = "Amazon", category = "Tablet",
                origin = "USA", price = 139.0, stock = 120,
                imageUrl = "https://tse2.mm.bing.net/th/id/OIP.5mdaA5NNf3SJqbTkS3QDQgHaFj?cb=ucfimg2&ucfimg=1&rs=1&pid=ImgDetMain&o=7&rm=3",
                description = "Read anytime, anywhere."
            ),
            ProductEntity(
                id = 0,
                name = "Elgato Stream Deck MK.2", brand = "Elgato", category = "Phụ kiện",
                origin = "Germany", price = 149.0, stock = 60,
                imageUrl = "https://tse3.mm.bing.net/th/id/OIP.Pl-hYD2glnFBepT3WcvLdQHaD-?w=314&h=180&c=7&r=0&o=7&cb=ucfimg2&dpr=1.3&pid=1.7&rm=3&ucfimg=1",
                description = "15 customizable LCD keys for streamers."
            ),
            ProductEntity(
                id = 0,
                name = "Sony a7 IV", brand = "Sony", category = "Máy ảnh",
                origin = "Japan", price = 2498.0, stock = 20,
                imageUrl = "https://th.bing.com/th/id/R.f740fe45e5ca33c56221220e169d10ac?rik=6GVwcRVehfDFQA&pid=ImgRaw&r=0",
                description = "A7 IV: Beyond basic."
            )
        )
    }
}
