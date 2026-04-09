export default function PartnerBox({ partner }) {
  return (
    <div className="flex items-center justify-center 
                    bg-white/5 border border-white/10 
                    rounded-xl p-6 hover:bg-white/10 transition">
      <img src={partner} alt="partner" className="h-10 object-contain" />
    </div>
  );
}