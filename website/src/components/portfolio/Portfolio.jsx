import { useState } from "react";

// Assets
import p1 from "../../assets/portfolio/p1.jpeg";
import p2 from "../../assets/portfolio/p2.jpeg";
import p3 from "../../assets/portfolio/p3.jpeg";
import p4 from "../../assets/portfolio/p4.jpeg";
import p5 from "../../assets/portfolio/p5.jpeg";
import p6 from "../../assets/portfolio/p6.jpeg";




export default function Portfolio() {
  const [filter, setFilter] = useState("all");

  const projects = [
    { id: 1, img: p1, tag: "branding", title: "Stock Management" },
    { id: 2, img: p2, tag: "web", title: "Expiry Alerts" },
    { id: 3, img: p3, tag: "illustrations", title: " Report " },
    { id: 3, img: p4, tag: "illustrations", title: " Report " },
    { id: 3, img: p5, tag: "illustrations", title: " Report " },
    { id: 3, img: p6, tag: "illustrations", title: " Report " },
  ];

  const filtered =
    filter === "all"
      ? projects
      : projects.filter((p) => p.tag === filter);

  return (
    <section id="portfolio" className="py-24 bg-black text-white">
      <div className="max-w-7xl mx-auto px-6">

        {/* TITLE */}
        <h2 className="text-4xl font-bold mb-10 text-center">
          App Features
        </h2>

        {/* FILTER */}
        <div className="flex justify-center gap-6 mb-10 text-sm text-gray-400">
          {["All Features",].map((f) => (
            <button
              key={f}
              onClick={() => setFilter(f)}
              className={`uppercase hover:text-white transition 
                ${filter === f ? "text-white font-semibold" : ""}`}
            >
              {f}
            </button>
          ))}
        </div>

        {/* GRID */}
        <div className="grid sm:grid-cols-2 md:grid-cols-3 gap-6">
  {filtered.map((p) => (
    <div
      key={p.id}
      className="group relative overflow-hidden rounded-2xl"
    >
      {/* aspect ratio wrapper */}
      <div className="aspect-[3/4]">
        <img
          src={p.img}
          alt={p.title}
          className="w-full h-full object-cover 
                     group-hover:scale-110 transition duration-500"
        />
      </div>

      {/* overlay */}
      <div className="absolute inset-0 bg-black/60 opacity-0 
                      group-hover:opacity-100 transition flex items-center justify-center">
        <h3 className="text-lg font-semibold text-white">
          {p.title}
        </h3>
      </div>
    </div>
  ))}
</div>

      </div>
    </section>
  );
}