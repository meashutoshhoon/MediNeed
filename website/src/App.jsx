import { useState, useEffect } from "react";

// Components
import DesktopNav from "./components/navbar/DesktopNav";
import MobileNav from "./components/navbar/MobileNav";
import Backdrop from "./components/navbar/Backdrop";
import Hero from "./components/hero/Hero";
import Portfolio from "./components/portfolio/Portfolio";
import Partners from "./components/partners/Partners";
import About from "./components/about/About";
import Contact from "./components/contact/Contact";
import Footer from "./components/footer/Footer";

export default function App() {
  const [userIsScrolled, setUserIsScrolled] = useState(false);
  const [mobileNavbarOpen, setMobileNavbarOpen] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setUserIsScrolled(window.pageYOffset > 80);
    };

    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  
  const toggleMobileMenu = () => {
    setMobileNavbarOpen(prev => !prev);
  };

  const closeMobileMenu = () => {
    setMobileNavbarOpen(false);
  };

  return (
    <div className="App">

     
      <MobileNav
        isOpen={mobileNavbarOpen}
        closeMobileMenu={closeMobileMenu}
      />

   
      <Backdrop
        isOpen={mobileNavbarOpen}
        closeMobileMenu={closeMobileMenu}
      />

      <DesktopNav
        userIsScrolled={userIsScrolled}
        mobileMenuOpen={toggleMobileMenu}
        mobileNavbarOpen={mobileNavbarOpen} 
      />

     
      <Hero />
      <Portfolio />
      <Partners />
      <About />
      <Contact />
      <Footer />

    </div>
  );
}