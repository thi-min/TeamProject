import Header from './Header';
import Footer from './Footer';
import Container from './Container';

const Layout = ({ children }) => {
  return (
    <>
      <Header />
      <main>
        <Container>{children}</Container>  
      </main>
      <Footer />
    </>
  );
};

export default Layout;