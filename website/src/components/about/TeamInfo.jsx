export default function TeamInfo() {
  return (
    <div className="flex items-center justify-center bg-gradient-to-br 
                    from-indigo-500/20 to-purple-500/20 
                    border border-white/10 rounded-2xl p-6">

      <div className="text-center">
        <h4 className="text-xl font-bold mb-3">Why This App?</h4>

        <p className="text-sm text-gray-300 mb-4">
          Managing medicines manually can lead to errors and missed expiry dates. This app provides a simple and reliable way to keep everything organized, ensuring better control and safety.
        </p>

        <button className="text-indigo-400 hover:underline">
          Explore Features
        </button>
      </div>

    </div>
  );
}