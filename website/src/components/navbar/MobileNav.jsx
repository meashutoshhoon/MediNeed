import CloseIcons from "../../assets/navbar/mobile-close.svg";
import Logo from "../../assets/navbar/logo-yellow.svg";

export default function MobileNav({ isOpen, closeMobileMenu }) {
  return (
    <div
      className={`fixed inset-0 bg-black z-50 flex flex-col items-center justify-center 
                  transition-transform duration-300 
                  ${isOpen ? "translate-x-0" : "translate-x-full"}`}
    >
      
      {/* CLOSE */}
      <button
        onClick={closeMobileMenu}
        className="absolute top-6 right-6"
      >
        <img src={CloseIcons} alt="close" />
      </button>

      {/* LOGO */}
      <h1 className="text-3xl  text-white font-bold cursor-pointer mb-10">
  ᗰEᗪIᑎEEᗪ
</h1>

      {/* MENU */}
      <ul className="space-y-6 text-white text-xl">
        {["download", "about", "contact"].map((item) => (
          <li key={item}>
            <a
              href={`#${item}`}
              onClick={closeMobileMenu}
              className="hover:text-grey-400  text-white transition"
            >
              {item.toUpperCase()}
            </a>
          </li>
        ))}
      </ul>

    </div>
  );
}