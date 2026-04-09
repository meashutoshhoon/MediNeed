import p6 from "../../assets/portfolio/p6.jpeg";

export default function Hero() {
  return (
    <section id="hero" className="min-h-screen bg-black text-white flex items-center">
      <div className="max-w-7xl mx-auto px-6 grid md:grid-cols-2 gap-12 items-center">

        {/* LEFT */}
        <div>
         

          <h1 className="text-5xl md:text-6xl font-bold mb-6">
  Manage Your Medicine Inventory <br /> Smartly.
</h1>

          <p className="text-gray-400 mb-8">
            Track stock, monitor expiry dates, and manage medicines efficiently — all in one simple app.
          </p>

          <button
          onClick={() => window.location.href = "https://github.com/meashutoshhoon/MediNeed/releases/download/v1.0.0/MediNeed-1.0.0-generic-release.apk"}
          className="px-6 py-3 bg-gradient-to-r from-indigo-500 to-purple-600 
                             rounded-xl font-semibold hover:scale-105 transition">
            Download for Free
          </button>
        </div>

        {/* RIGHT */}
        <div className="flex justify-center">
          <img
            src={p6}
            alt="hero"
            className="max-w-md w-full"
          />
        </div>

      </div>
    </section>
  );
}