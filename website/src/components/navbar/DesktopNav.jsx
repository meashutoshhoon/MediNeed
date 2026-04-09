import LogoImg from "../../assets/navbar/logo.svg";
import MobileMenuIcon from "../../assets/navbar/mobile-menu.svg";
import Logo from "../../assets/footer/logo.svg";
import logo1 from "../../assets/Medineedlogo/logo1.svg"

export default function DesktopNav({ userIsScrolled, mobileMenuOpen }) {
  return (
    <nav
      className={`fixed w-full z-50 transition-all duration-300 
      ${userIsScrolled ? "bg-black/80 backdrop-blur border-b border-white/10" : "bg-transparent"}`}
    >
      <div className="max-w-7xl mx-auto px-6 flex justify-between items-center h-16">

        {/* LOGO */}
        <h1 className="text-3xl  text-white font-bold cursor-pointer">
  ᗰEᗪIᑎEEᗪ
</h1>

        {/* MOBILE BUTTON */}
        <button onClick={mobileMenuOpen} className="md:hidden">
          <img src={MobileMenuIcon} alt="menu" />
        </button>

        {/* DESKTOP MENU */}
        <ul className="hidden md:flex gap-8 text-sm text-gray-300">
          {["download", "about", "contact"].map((item) => (
            <li key={item}>
              <a
                href={`#${item}`}
                className="hover:text-white transition"
              >
                {item.toUpperCase()}
              </a>
            </li>
          ))}
        </ul>

      </div>
    </nav>
  );
}