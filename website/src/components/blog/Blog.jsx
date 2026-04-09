import { useState } from "react";
import { Swiper, SwiperSlide } from "swiper/react";
import "swiper/css";

import Title from "../ui-components/title/Title";
import BlogBox from "./BlogBox";

// Images
import Preview01 from "../../assets/blog/story01/preview.png";
import Preview02 from "../../assets/blog/story02/preview.png";
import Preview03 from "../../assets/blog/story03/preview.png";
import Preview04 from "../../assets/blog/story04/preview.png";
import Preview05 from "../../assets/blog/story05/preview.png";
import Preview06 from "../../assets/blog/story06/preview.png";

export default function Blog() {
  const [stories] = useState([
    {
      image: Preview01,
      id: "1",
      title: "SUPER BLOG ARTICLE!",
      description: "Lorem ipsum dolor sit amet...",
      date: "21 April 2020",
    },
    {
      image: Preview02,
      id: "2",
      title: "AWESOME ARTICLE!",
      description: "Lorem ipsum dolor...",
      date: "27 April 2020",
    },
    {
      image: Preview03,
      id: "3",
      title: "SUPER TITLE!",
      description: "Lorem ipsum...",
      date: "03 May 2020",
    },
    {
      image: Preview04,
      id: "4",
      title: "BLOG TITLE!",
      description: "Lorem ipsum...",
      date: "15 May 2020",
    },
    {
      image: Preview05,
      id: "5",
      title: "BLOG ARTICLE!",
      description: "Lorem ipsum...",
      date: "20 May 2020",
    },
    {
      image: Preview06,
      id: "6",
      title: "AWESOME TITLE!",
      description: "Lorem ipsum...",
      date: "23 May 2020",
    },
  ]);

  return (
    <section id="blog" className="py-24 bg-black text-white">
      <div className="max-w-7xl mx-auto px-6 text-center">

        <Title title="OUR BLOG." />

        <p className="text-gray-400 text-sm mt-4 mb-12">
          Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        </p>

        <Swiper
          spaceBetween={20}
          loop={true}
          breakpoints={{
            640: { slidesPerView: 1 },
            768: { slidesPerView: 2 },
            1024: { slidesPerView: 3 },
          }}
        >
          {stories.map((story) => (
            <SwiperSlide key={story.id}>
              <BlogBox article={story} />
            </SwiperSlide>
          ))}
        </Swiper>

      </div>
    </section>
  );
}