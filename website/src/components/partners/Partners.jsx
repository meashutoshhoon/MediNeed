import { Swiper, SwiperSlide } from "swiper/react";
import "swiper/css";

import PartnerBox from "./PartnerBox";

// Assets
import Partner01 from "../../assets/partners/partner01.svg";
import Partner02 from "../../assets/partners/partner02.svg";
import Partner03 from "../../assets/partners/partner03.svg";
import Partner04 from "../../assets/partners/partner04.svg";
import Partner05 from "../../assets/partners/partner05.svg";
import Partner06 from "../../assets/partners/partner06.svg";

export default function Partners() {
  const partners = [
    Partner01,
    Partner02,
    Partner03,
    Partner04,
    Partner05,
    Partner06,
  ];

  return (
    <section id="partners" className="py-20 bg-black text-white">
      <div className="max-w-7xl mx-auto px-6">

        <Swiper
          spaceBetween={20}
          loop={true}
          breakpoints={{
            320: { slidesPerView: 2 },
            640: { slidesPerView: 3 },
            768: { slidesPerView: 4 },
            1024: { slidesPerView: 5 },
            1280: { slidesPerView: 6 },
          }}
        >
          {partners.map((p, i) => (
            <SwiperSlide key={i}>
              <PartnerBox partner={p} />
            </SwiperSlide>
          ))}
        </Swiper>

      </div>
    </section>
  );
}