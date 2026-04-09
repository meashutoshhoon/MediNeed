export default function ErrorModal({ closeModal }) {
  return (
    <div className="bg-white text-black rounded-2xl p-8 max-w-sm w-full text-center shadow-xl animate-[fadeUp_0.4s_ease]">

      <h2 className="text-2xl font-bold mb-3">Oops!</h2>

      <p className="text-gray-600 mb-6">
        Something went wrong. Please try again.
      </p>

      <button
        onClick={closeModal}
        className="px-6 py-2 rounded-lg bg-black text-white hover:scale-105 transition"
      >
        OK
      </button>

    </div>
  );
}