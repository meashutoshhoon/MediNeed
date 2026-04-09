export default function ContactInfoBox({ icon, textLine1, textLine2 }) {
  return (
    <div className="flex items-center gap-4 bg-white/5 border border-white/10 
                    rounded-xl p-4 hover:bg-white/10 transition">

      <img src={icon} alt="icon" className="w-8 h-8" />

      <div className="text-sm text-gray-300">
        <p>{textLine1}</p>
        {textLine2 && <p>{textLine2}</p>}
      </div>

    </div>
  );
}