'use client'

import React, {useState} from 'react';
import {motion, useScroll, useTransform} from 'framer-motion';
import {
  ArrowRight,
  CheckCircle,
  ChevronDown,
  Clock,
  FileText,
  Menu,
  PenTool,
  Search,
  Star,
  Target,
  TrendingUp,
  X,
  Zap
} from 'lucide-react';
import {useRouter} from "next/navigation";

const LandingPage = () => {
  const router = useRouter();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const {scrollYProgress} = useScroll();
  const yPosAnim = useTransform(scrollYProgress, [0, 1], [0, -100]);

  const features = [
    {
      icon: <Search className="w-8 h-8"/>,
      title: "Opportunity Matcher",
      description: "Intelligently filters through all available tenders to identify those that align with your company’s domain expertise, budget constraints—eliminating hours of manual review and ensuring you never miss a relevant opportunity.",
      color: "from-blue-500 to-cyan-500"
    },
    {
      icon: <FileText className="w-8 h-8"/>,
      title: "Requirement Extractor",
      description: "Quickly extracts and organizes essential details from uploaded RFPs—such as submission deadlines, evaluation criteria, and required documents—to accelerate decision-making and simplify proposal preparation.",
      color: "from-purple-500 to-pink-500"
    },
    {
      icon: <PenTool className="w-8 h-8"/>,
      title: "AI-Powered Proposal Composer",
      description: "Generates complete, structured proposals using your previous winning bids and internal templates.",
      color: "from-green-500 to-emerald-500"
    },
    {
      icon: <CheckCircle className="w-8 h-8"/>,
      title: "Compliance Checker",
      description: "Reviews your draft proposals and flags missing mandatory items before submission.",
      color: "from-orange-500 to-red-500"
    }
  ];

  const stats = [
    {number: "85%", label: "Higher Win Rate", icon: <TrendingUp/>},
    {number: "12x", label: "Faster Processing", icon: <Zap/>},
    {number: "99%", label: "Compliance Score", icon: <Target/>},
    {number: "200+", label: "Hours Saved Monthly", icon: <Clock/>}
  ];

  const handleFeatureClick = (title) => {
    if (title === 'Opportunity Matcher') router.push(`/rfp-filter`);
  }

  return (
    <div
      className="min-h-screen bg-gradient-to-br from-slate-900 via-purple-900 to-slate-900 text-white overflow-hidden">
      {/* Animated Background */}
      <div className="fixed inset-0 overflow-hidden pointer-events-none">
        <motion.div
          className="absolute -top-40 -right-40 w-96 h-96 bg-purple-500 rounded-full mix-blend-multiply filter blur-xl opacity-20"
          animate={{rotate: 360}}
          transition={{duration: 20, repeat: Infinity, ease: "linear"}}
        />
        <motion.div
          className="absolute -bottom-40 -left-40 w-96 h-96 bg-blue-500 rounded-full mix-blend-multiply filter blur-xl opacity-20"
          animate={{rotate: -360}}
          transition={{duration: 25, repeat: Infinity, ease: "linear"}}
        />
      </div>

      {/* Navigation */}
      <nav className="relative z-50 px-6 py-4">
        <div className="max-w-7xl mx-auto flex items-center justify-between">
          <motion.div
            className="text-2xl font-bold bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent"
            initial={{opacity: 0, x: -20}}
            animate={{opacity: 1, x: 0}}
            transition={{duration: 0.6}}
          >
            RFP Copilot
          </motion.div>

          <div className="hidden md:flex items-center space-x-8">
            {['Features', 'Benefits', 'Pricing', 'Contact'].map((item, index) => (
              <motion.a
                key={item}
                href={`#${item.toLowerCase()}`}
                className="hover:text-blue-400 transition-colors cursor-pointer"
                initial={{opacity: 0, y: -20}}
                animate={{opacity: 1, y: 0}}
                transition={{duration: 0.6, delay: index * 0.1}}
              >
                {item}
              </motion.a>
            ))}
            <motion.button
              className="bg-gradient-to-r from-blue-500 to-purple-600 px-6 py-2 rounded-full hover:from-blue-600 hover:to-purple-700 transition-all duration-300 shadow-lg hover:shadow-xl"
              initial={{opacity: 0, y: -20}}
              animate={{opacity: 1, y: 0}}
              transition={{duration: 0.6, delay: 0.4}}
              whileHover={{scale: 1.05}}
              whileTap={{scale: 0.95}}
            >
              Get Started
            </motion.button>
          </div>

          <button
            className="md:hidden"
            onClick={() => setIsMenuOpen(!isMenuOpen)}
          >
            {isMenuOpen ? <X/> : <Menu/>}
          </button>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="relative px-6 py-20">
        <div className="max-w-7xl mx-auto text-center">
          <motion.div
            initial={{opacity: 0, y: 30}}
            animate={{opacity: 1, y: 0}}
            transition={{duration: 0.8}}
            className="mb-8"
          >
            <h1
              className="text-5xl md:text-7xl font-bold mb-6 bg-gradient-to-r from-white via-blue-200 to-purple-200 bg-clip-text text-transparent leading-tight">
              Transform Your
              <br/>
              <span className="bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent">
                RFP Response
              </span>
            </h1>
            <p className="text-xl md:text-2xl text-gray-300 max-w-3xl mx-auto leading-relaxed">
              AI-powered agent that helps companies win more government tenders by focusing on strategy—not
              paperwork
            </p>
          </motion.div>

          <motion.div
            initial={{opacity: 0, y: 30}}
            animate={{opacity: 1, y: 0}}
            transition={{duration: 0.8, delay: 0.2}}
            className="flex flex-col sm:flex-row gap-4 justify-center items-center mb-16"
          >
            <motion.button
              className="bg-gradient-to-r from-blue-500 to-purple-600 px-8 py-4 rounded-full text-lg font-semibold hover:from-blue-600 hover:to-purple-700 transition-all duration-300 shadow-xl hover:shadow-2xl flex items-center gap-2"
              whileHover={{scale: 1.05, y: -2}}
              whileTap={{scale: 0.95}}
            >
              Start Free Trial
              <ArrowRight className="w-5 h-5"/>
            </motion.button>
            <motion.button
              className="border-2 border-gray-600 px-8 py-4 rounded-full text-lg font-semibold hover:border-white hover:bg-white hover:text-black transition-all duration-300"
              whileHover={{scale: 1.05}}
              whileTap={{scale: 0.95}}
            >
              Watch Demo
            </motion.button>
          </motion.div>

          {/* Stats */}
          <motion.div
            initial={{opacity: 0, y: 40}}
            animate={{opacity: 1, y: 0}}
            transition={{duration: 0.8, delay: 0.4}}
            className="grid grid-cols-2 md:grid-cols-4 gap-8 max-w-4xl mx-auto"
          >
            {stats.map((stat, index) => (
              <motion.div
                key={stat.label}
                className="text-center"
                initial={{opacity: 0, y: 20}}
                animate={{opacity: 1, y: 0}}
                transition={{duration: 0.6, delay: 0.5 + index * 0.1}}
              >
                <div className="flex justify-center mb-2 text-blue-400">
                  {stat.icon}
                </div>
                <div
                  className="text-3xl md:text-4xl font-bold bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent">
                  {stat.number}
                </div>
                <div className="text-gray-400 text-sm">{stat.label}</div>
              </motion.div>
            ))}
          </motion.div>
        </div>

        {/* Scroll Indicator */}
        <motion.div
          className="absolute bottom-10 left-1/2 transform -translate-x-1/2"
          animate={{y: [0, 10, 0]}}
          transition={{duration: 2, repeat: Infinity}}
        >
          <ChevronDown className="w-6 h-6 text-gray-400"/>
        </motion.div>
      </section>

      {/* Features Section */}
      <section id="features" className="relative px-6 py-20">
        <div className="max-w-7xl mx-auto">
          <motion.div
            initial={{opacity: 0, y: 30}}
            whileInView={{opacity: 1, y: 0}}
            transition={{duration: 0.8}}
            className="text-center mb-16"
          >
            <h2
              className="text-4xl md:text-5xl font-bold mb-6 bg-gradient-to-r from-white to-gray-300 bg-clip-text text-transparent">
              Four Powerful Modules
            </h2>
            <p className="text-xl text-gray-400 max-w-3xl mx-auto">
              Each module is designed to eliminate bottlenecks and maximize your team's strategic focus
            </p>
          </motion.div>

          <div className="grid md:grid-cols-2 gap-8">
            {features.map((feature, index) => (
              <motion.div
                key={feature.title}
                initial={{opacity: 0, y: 40}}
                whileInView={{opacity: 1, y: 0}}
                transition={{duration: 0.6, delay: index * 0.2}}
                whileHover={{y: -10, scale: 1.02}}
                className="relative group cursor-pointer"
                onClick={() => handleFeatureClick(feature.title)}
              >
                <div
                  className="absolute inset-0 bg-gradient-to-r opacity-0 group-hover:opacity-100 transition-opacity duration-300 rounded-2xl blur-xl"
                  style={{background: `linear-gradient(135deg, ${feature.color.split(' ')[1]}, ${feature.color.split(' ')[3]})`}}/>

                <div
                  className="relative bg-gray-800/50 backdrop-blur-sm border border-gray-700 rounded-2xl p-8 hover:border-gray-600 transition-all duration-300">
                  <div
                    className={`w-16 h-16 rounded-2xl bg-gradient-to-r ${feature.color} flex items-center justify-center mb-6 text-white`}>
                    {feature.icon}
                  </div>
                  <h3 className="text-2xl font-bold mb-4 text-white">{feature.title}</h3>
                  <p className="text-gray-300 leading-relaxed">{feature.description}</p>
                </div>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Benefits Section */}
      <section id="benefits" className="relative px-6 py-20 bg-gradient-to-r from-blue-900/20 to-purple-900/20">
        <div className="max-w-7xl mx-auto">
          <motion.div
            initial={{opacity: 0, y: 30}}
            whileInView={{opacity: 1, y: 0}}
            transition={{duration: 0.8}}
            className="text-center mb-16"
          >
            <h2
              className="text-4xl md:text-5xl font-bold mb-6 bg-gradient-to-r from-white to-gray-300 bg-clip-text text-transparent">
              Why Choose RFP Copilot?
            </h2>
          </motion.div>

          <div className="grid md:grid-cols-3 gap-8">
            {[
              {
                title: "Increase Win Rates",
                description: "AI-powered analysis ensures your proposals meet all requirements and stand out from competitors.",
                icon: <Star className="w-8 h-8"/>
              },
              {
                title: "Save Time & Resources",
                description: "Automate tedious tasks and free up your team to focus on strategy and relationship building.",
                icon: <Clock className="w-8 h-8"/>
              },
              {
                title: "Ensure Compliance",
                description: "Never miss a requirement again with our comprehensive compliance checking system.",
                icon: <CheckCircle className="w-8 h-8"/>
              }
            ].map((benefit, index) => (
              <motion.div
                key={benefit.title}
                initial={{opacity: 0, y: 40}}
                whileInView={{opacity: 1, y: 0}}
                transition={{duration: 0.6, delay: index * 0.2}}
                whileHover={{y: -5}}
                className="text-center group"
              >
                <div
                  className="w-20 h-20 bg-gradient-to-r from-blue-500 to-purple-600 rounded-full flex items-center justify-center mx-auto mb-6 group-hover:scale-110 transition-transform duration-300">
                  {benefit.icon}
                </div>
                <h3 className="text-2xl font-bold mb-4">{benefit.title}</h3>
                <p className="text-gray-300 leading-relaxed">{benefit.description}</p>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="relative px-6 py-20">
        <div className="max-w-4xl mx-auto text-center">
          <motion.div
            initial={{opacity: 0, y: 30}}
            whileInView={{opacity: 1, y: 0}}
            transition={{duration: 0.8}}
          >
            <h2
              className="text-4xl md:text-5xl font-bold mb-6 bg-gradient-to-r from-white via-blue-200 to-purple-200 bg-clip-text text-transparent">
              Ready to Transform Your RFP Process?
            </h2>
            <p className="text-xl text-gray-300 mb-8 max-w-2xl mx-auto">
              Join hundreds of companies already winning more tenders with RFP Copilot
            </p>
            <motion.button
              className="bg-gradient-to-r from-blue-500 to-purple-600 px-10 py-5 rounded-full text-xl font-semibold hover:from-blue-600 hover:to-purple-700 transition-all duration-300 shadow-2xl hover:shadow-3xl flex items-center gap-3 mx-auto"
              whileHover={{scale: 1.05, y: -3}}
              whileTap={{scale: 0.95}}
            >
              Start Your Free Trial Today
              <ArrowRight className="w-6 h-6"/>
            </motion.button>
          </motion.div>
        </div>
      </section>

      {/* Footer */}
      <footer className="relative px-6 py-12 border-t border-gray-800">
        <div className="max-w-7xl mx-auto text-center">
          <div
            className="text-2xl font-bold bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent mb-4">
            RFP Copilot
          </div>
          <p className="text-gray-400 mb-6">
            Empowering companies to win more government tenders with AI
          </p>
          <div className="flex justify-center space-x-8 text-gray-400">
            <a href="#" className="hover:text-white transition-colors">Privacy</a>
            <a href="#" className="hover:text-white transition-colors">Terms</a>
            <a href="#" className="hover:text-white transition-colors">Contact</a>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default LandingPage;