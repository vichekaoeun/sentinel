# Sentinel Logo Implementation

## 🎨 Logo Design

The Sentinel logo features a powerful, crowned figure representing vigilance and authority:

- **Crown**: Split design with yellow (left) and gold/beige (right) sections
- **Face**: Dark trapezoidal shape representing strength and mystery
- **Eyes**: Contrasting white (left) and red (right) triangles symbolizing vigilance and alertness
- **Background**: Black for a professional, authoritative appearance

## 📍 Usage

### Components

1. **SentinelLogo Component** (`src/components/SentinelLogo.js`)
   - Reusable SVG component
   - Customizable size via `size` prop
   - Customizable styling via `className` prop

2. **Header Integration** (`src/components/Header.js`)
   - Logo displayed in the top navigation bar
   - Size: 40x40 pixels
   - Positioned next to "Sentinel" text

3. **Loading Screen** (`src/components/Dashboard.js`)
   - Animated logo during page load
   - Size: 64x64 pixels with pulse animation

4. **Browser Tab** (`public/index.html`)
   - Custom favicon using the logo
   - Inline SVG data URL for immediate loading

## 🎯 Branding Elements

- **Page Title**: "Sentinel - Risk Management System"
- **Loading Text**: "Loading Sentinel Dashboard..."
- **Color Scheme**: 
  - Primary: Black (#000000)
  - Crown Left: Yellow (#FFD700)
  - Crown Right: Gold (#F4A460)
  - Face: Dark Gray (#2C2C2C)
  - Left Eye: White (#FFFFFF)
  - Right Eye: Red (#FF0000)

## 🔧 Customization

To modify the logo:

1. **Colors**: Update the fill values in `SentinelLogo.js`
2. **Size**: Use the `size` prop when implementing the component
3. **Animation**: Add CSS classes via the `className` prop
4. **Favicon**: Update the SVG data URL in `index.html`

## 📱 Responsive Design

The logo scales appropriately across different screen sizes:
- Desktop: 40px in header, 64px in loading screen
- Mobile: Automatically scales with viewport
- High DPI: SVG ensures crisp rendering at all resolutions
