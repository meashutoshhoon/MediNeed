import Logo from "../../assets/footer/logo.svg";
import Arrow from "../../assets/footer/arrow.svg";

export default function Footer() {
  return (
    <footer className="bg-black text-white border-t border-white/10 py-10">
      <div className="max-w-7xl mx-auto px-6 flex flex-col md:flex-row justify-between items-center gap-6">

        {/* LEFT */}
        <div className="text-center md:text-left">
          <h1 className="text-3xl  text-white font-bold cursor-pointer">
  ᗰEᗪIᑎEEᗪ
</h1>
          <p className="text-sm text-gray-400">
            © 2026 MediNeed. All Rights Reserved
          </p>
        </div>

        {/* RIGHT */}
        <button
          onClick={() => window.scrollTo({ top: 0, behavior: "smooth" })}
          className="flex items-center gap-2 text-sm text-gray-400 hover:text-white transition"
        >
          BACK TO TOP
          <img src={Arrow} alt="arrow" className="w-4" />
        </button>

      </div>
    </footer>
  );
}