export default function Title({ title, center = true }) {
  return (
    <div className={`mb-12 ${center ? "text-center" : ""}`}>
      <h2 className="text-4xl md:text-5xl font-bold tracking-tight">
        {title}
      </h2>

      {/* subtle underline */}
      <div className="mt-4 h-1 w-16 bg-gradient-to-r from-indigo-500 to-purple-600 mx-auto rounded-full"></div>
    </div>
  );
}