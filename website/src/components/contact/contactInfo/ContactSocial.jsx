import Facebook from "../../../assets/contact/facebook.svg";
import Twitter from "../../../assets/contact/twitter.svg";
import Dribble from "../../../assets/contact/dribble.svg";

export default function ContactSocial() {
  return (
    <div className="flex justify-center gap-6 mt-10">

      {[Facebook, Twitter, Dribble].map((icon, i) => (
        <div
          key={i}
          className="w-10 h-10 flex items-center justify-center 
                     bg-white/5 border border-white/10 rounded-full 
                     hover:bg-white/10 transition cursor-pointer"
        >
          <img src={icon} alt="social" className="w-5" />
        </div>
      ))}

    </div>
  );
}