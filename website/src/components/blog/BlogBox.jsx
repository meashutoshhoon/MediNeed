export default function BlogBox({ article }) {
  return (
    <div className="group bg-white/5 border border-white/10 rounded-2xl overflow-hidden 
                    hover:bg-white/10 transition duration-300">

      {/* IMAGE */}
      <div className="relative overflow-hidden">
        <img
          src={article.image}
          alt="blog"
          className="w-full h-56 object-cover group-hover:scale-110 transition duration-500"
        />

        {/* HOVER OVERLAY */}
        <div className="absolute inset-0 bg-black/60 flex items-center justify-center 
                        opacity-0 group-hover:opacity-100 transition">
          <h4 className="text-xl font-bold">READ MORE</h4>
        </div>
      </div>

      {/* CONTENT */}
      <div className="p-5 text-left">
        <h4 className="font-semibold text-lg mb-2">{article.title}</h4>

        <p className="text-sm text-gray-400 mb-3">
          {article.description}
        </p>

        <p className="text-xs text-gray-500">
          {article.date}
        </p>
      </div>

    </div>
  );
}