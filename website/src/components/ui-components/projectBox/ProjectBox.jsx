export default function ProjectBox({ preview, title, tag }) {
  return (
    <div className="group relative overflow-hidden rounded-2xl border border-white/10">

      {/* IMAGE */}
      <img
        src={preview}
        alt={title}
        className="w-full h-64 object-cover 
                   group-hover:scale-110 transition duration-500"
      />

      {/* OVERLAY */}
      <div className="absolute inset-0 bg-black/70 opacity-0 
                      group-hover:opacity-100 transition flex items-center justify-center">

        <div className="text-center">
          <p className="text-xl font-bold">{title}</p>
          <p className="text-sm text-gray-400">{tag}</p>
        </div>

      </div>

    </div>
  );
}