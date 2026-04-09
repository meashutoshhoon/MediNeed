import { useState } from "react";
import Title from "../ui-components/title/Title";
import ContactInfo from "./contactInfo/ContactInfo";
import ContactSocial from "./contactInfo/ContactSocial";

import ContactBackground from "../../assets/contact/geto.png";

export default function Contact() {
  const [form, setForm] = useState({
    name: "",
    email: "",
    message: "",
  });

  const [sending, setSending] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setSending(true);

    // simulate API call
    setTimeout(() => {
      alert("Message sent!");
      setSending(false);
      setForm({ name: "", email: "", message: "" });
    }, 1500);
  };

  return (
    <section id="contact" className="py-24 bg-black text-white">
      <div className="max-w-7xl mx-auto px-6">

        <Title title="Get in Touch" />

        <p className="text-gray-400 text-sm text-center mb-12">
          Have questions or need support? Reach out to us anytime — we're here to help.
        </p>

        {/* FORM + IMAGE */}
        <div className="grid md:grid-cols-2 gap-12 items-center">

          {/* FORM */}
          <form onSubmit={handleSubmit} className="space-y-4">

            <h4 className="text-2xl font-bold mb-4">
              Send Us Message
            </h4>

            <input
              type="text"
              name="name"
              placeholder="Name"
              value={form.name}
              onChange={handleChange}
              required
              className="w-full p-3 rounded-lg bg-white/5 border border-white/10 
                         focus:outline-none focus:border-indigo-500"
            />

            <input
              type="email"
              name="email"
              placeholder="Email"
              value={form.email}
              onChange={handleChange}
              required
              className="w-full p-3 rounded-lg bg-white/5 border border-white/10 
                         focus:outline-none focus:border-indigo-500"
            />

            <textarea
              name="message"
              rows="5"
              placeholder="Message..."
              value={form.message}
              onChange={handleChange}
              required
              className="w-full p-3 rounded-lg bg-white/5 border border-white/10 
                         focus:outline-none focus:border-indigo-500"
            />

            <button
              type="submit"
              disabled={sending}
              className="w-full py-3 rounded-xl bg-gradient-to-r from-indigo-500 to-purple-600 
                         font-semibold hover:scale-105 transition"
            >
              {sending ? "Sending..." : "Send Message"}
            </button>

          </form>

          {/* IMAGE */}
          <div className="flex justify-center">
            <img
              src={ContactBackground}
              alt="contact"
              className="max-w-md w-full"
            />
          </div>

        </div>

        {/* INFO + SOCIAL */}
        

      </div>
    </section>
  );
}