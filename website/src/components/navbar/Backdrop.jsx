export default function Backdrop({ isOpen, closeMobileMenu }) {
  return (
    <div
      onClick={closeMobileMenu}
      className={`fixed inset-0 bg-black/60 backdrop-blur-sm z-40 
                  transition-opacity duration-300 
                  ${isOpen ? "opacity-100 visible" : "opacity-0 invisible"}`}
    />
  );
}