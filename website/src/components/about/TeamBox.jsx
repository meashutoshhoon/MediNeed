export default function TeamBox({ avatar, name, job }) {
  return (
    <div className="flex flex-col items-center bg-white/5 border border-white/10 
                    rounded-2xl p-6 hover:bg-white/10 transition">

      <img
        src={avatar}
        alt={name}
        className="w-24 h-24 object-cover rounded-full mb-4"
      />

      <div className="text-center">
        <p className="text-lg font-semibold">{name}</p>
        <p className="text-sm text-gray-400">{job}</p>
      </div>

    </div>
  );
}