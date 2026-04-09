export default function Button({ label, target }) {
  const handleClick = () => {
    const el = document.getElementById(target);
    if (el) {
      el.scrollIntoView({ behavior: "smooth" });
    }
  };

  return (
    <button
      onClick={handleClick}
      className="px-6 py-3 rounded-xl font-semibold 
                 bg-gradient-to-r from-indigo-500 to-purple-600 
                 text-white shadow-lg 
                 hover:scale-105 hover:shadow-indigo-500/40 
                 transition-all duration-300"
    >
      {label}
    </button>
  );
}