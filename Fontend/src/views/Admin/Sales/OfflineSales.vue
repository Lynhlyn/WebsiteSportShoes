<template>
      <div class="container mx-auto">
            <div class="row g-4 justify-content-center">
                  <div class="col-md-8">
                        <div class="bg-white p-6 rounded-lg shadow-lg mb-8 border border-gray-300">
                              <h3 class="font-semibold text-2xl mb-4">Danh Sách Hóa Đơn <button class="btn btn-primary "
                                          @click="createInvoice">🆕 Tạo Hóa Đơn</button></h3>

                              <!-- Lưới (Grid) hiển thị danh sách hóa đơn -->
                              <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                                    <div v-for="(invoice, index) in paginatedInvoices" :key="invoice.maDonHang"
                                          class="invoice-card cursor-pointer hover:bg-gray-200"
                                          :class="{ 'bg-gray-400': selectedInvoice.maDonHang === invoice.maDonHang }"
                                          @click="selectInvoice(invoice)">

                                          <!-- Dấu X để hủy hóa đơn -->
                                          <button @click.stop="cancelInvoice(invoice)" class="cancel-btn">
                                                <i class="bi bi-x-circle"></i> <!-- Sử dụng icon cho dấu X -->
                                          </button>

                                          <!-- Mã hóa đơn và thông tin -->
                                          <h5 class="font-semibold text-lg text-gray-800 truncate">{{ invoice.maDonHang
                                                }}</h5>
                                          <p><strong>Khách hàng:</strong> {{ invoice.khachHang ? invoice.khachHang.hoTen
                                                : 'Không có' }}</p>
                                          <p><strong>Tổng tiền:</strong> {{ invoice.tongTien ?
                                                invoice.tongTien.toLocaleString() : '0 ' }}đ </p>
                                    </div>
                              </div>

                              <!-- Pagination -->
                              <div class="pagination-container mt-6" v-if="filteredInvoices.length > 0">
                                    <button @click="prevPageInvoice" :disabled="currentPageInvoice === 1"
                                          class="pagination-btn">
                                          <i class="bi bi-chevron-left"></i>
                                    </button>

                                    <div class="pagination-pages">
                                          <button v-for="page in visiblePagesInvoice" :key="page"
                                                @click="changePageInvoice(page)"
                                                :class="['pagination-page', { 'active': currentPageInvoice === page }]">
                                                {{ page }}
                                          </button>
                                    </div>

                                    <button @click="nextPageInvoice"
                                          :disabled="currentPageInvoice === totalPagesInvoice" class="pagination-btn">
                                          <i class="bi bi-chevron-right"></i>
                                    </button>

                                    <select v-model="pageSizeInvoice" class="pagination-select"
                                          @change="changePageInvoice(1)">
                                          <option v-for="option in [5, 10, 15, 20]" :key="option" :value="option">
                                                {{ option }} / trang
                                          </option>
                                    </select>
                              </div>
                        </div>






                        <!-- Giỏ Hàng -->
                        <div class="bg-white p-4 rounded-lg shadow-lg mb-8 border border-gray-300">
                              <h3 class="font-semibold text-xl mb-4">Giỏ Hàng</h3>

                              <!-- Hiển thị chi tiết sản phẩm trong giỏ hàng -->
                              <div v-if="orderDetails.length > 0">
                                    <div class="cart-items">
                                          <div v-for="(spct, index) in orderDetails" :key="spct.id" class="cart-item">

                                                <!-- Nút "Xóa" thay bằng dấu "X" -->
                                                <button @click="removeFromOrder(spct.id)" class="btn-remove">
                                                      <i class="bi bi-x-circle"></i>
                                                </button>

                                                <div class="item-info">


                                                      <div class="item-details">
                                                            <h5 class="item-name">{{ spct.tenSanPham || 'N/A' }}</h5>
                                                            <p class="item-code"><strong>Mã sản phẩm:</strong> {{
                                                                  spct.maSPCT || 'N/A' }}</p>
                                                      </div>
                                                      <div class="quantity-actions">
                                                            <input v-model="spct.soLuong" type="number" min="1"
                                                                  @input="updateQuantity(spct)"
                                                                  class="quantity-input" />
                                                      </div>
                                                      <!-- Các phần liên quan đến giá, số lượng và thành tiền -->


                                                      <p class="item-total"> {{ (spct.soLuong * (spct.giaBan ||
                                                            0)).toLocaleString() }} đ</p>
                                                </div>
                                          </div>
                                    </div>
                              </div>

                              <div v-if="orderDetails.length === 0">
                                    <p class="text-center">Không có sản phẩm trong giỏ hàng.</p>
                              </div>

                              <div v-if="loading" class="text-center">Đang tải...</div>

                              <div v-if="!loading" class="text-right font-bold mt-4">
                                    <p>Tổng Tiền: <span class="total-amount">{{ totalAmount.toLocaleString() }} đ</span>
                                    </p>
                              </div>
                        </div>

                        <!-- Tìm Kiếm Sản Phẩm -->
                        <div class="bg-white p-6 rounded-lg shadow-lg mb-8 border border-gray-300">
                              <h3 class="font-semibold text-2xl mb-4">Danh sách sản phẩm </h3>
                              <input type="text" v-model="searchQuerySPCT" @input="searchProducts"
                                    class="form-control mb-4 border rounded-lg p-2"
                                    placeholder="Tìm kiếm theo tên sản phẩm..." />
                              <table v-if="!loading && paginatedSanPhamCTList.length > 0"
                                    class="table table-striped table-hover">
                                    <thead class="table-dark">
                                          <tr class="text-center">
                                                <th>Mã</th>
                                                <th>Tên sản phẩm</th>
                                                <th>Màu sắc</th>
                                                <th>Size</th>
                                                <th>Chất liệu</th>
                                                <th>Danh Mục</th>
                                                <th>Thương hiệu</th>
                                                <th>Đế giày</th>
                                                <th>Số lượng</th>
                                                <th>Giá Bán</th>
                                          </tr>
                                    </thead>
                                    <tbody>
                                          <tr v-for="(spct, index) in paginatedSanPhamCTList" :key="spct.id"
                                                class="align-middle" @click="addToCart(spct)">
                                                <td>{{ spct.maSPCT }}</td>
                                                <td>{{ spct.tenSanPham }}</td>

                                                <td>{{ spct.tenMau || "Không có" }}</td>

                                                <td class="text-center">{{ spct.tenSize }}</td>
                                                <td class="text-center">{{ spct.tenDeGiay ? spct.tenDeGiay : "Không có"
                                                }}</td>
                                                <td class="text-center">{{ spct.tenThuongHieu ? spct.tenThuongHieu :
                                                      "Không có" }}</td>
                                                <td class="text-center">{{ spct.tenDanhMuc ? spct.tenDanhMuc : "Không có" }}</td>
                                                <td class="text-center">{{ spct.tenChatLieu ? spct.tenChatLieu : "Không có" }}</td>
                                                <td class="text-center">{{ spct.soLuong }}</td>
                                                <td class="text-center">{{ spct.giaBan.toLocaleString() }} đ</td>
                                          </tr>
                                    </tbody>
                              </table>
                              <div class="pagination-container" v-if="sanPhamCTList.length > 0">
                                    <button @click="prevPage" :disabled="currentPageSPCT === 1" class="pagination-btn">
                                          <i class="bi bi-chevron-left"></i>
                                    </button>
                                    <div class="pagination-pages">
                                          <button v-for="page in visiblePages" :key="page" @click="changePage(page)"
                                                :class="['pagination-page', { 'active': currentPageSPCT === page }]">
                                                {{ page }}
                                          </button>
                                    </div>
                                    <button @click="nextPage" :disabled="currentPageSPCT === totalPages"
                                          class="pagination-btn">
                                          <i class="bi bi-chevron-right"></i>
                                    </button>
                                    <select v-model="pageSizeSPCT" class="pagination-select"
                                          @change="currentPageSPCT = 1">
                                          <option v-for="option in [5, 10, 15, 20]" :key="option" :value="option">
                                                {{ option }} / trang
                                          </option>
                                    </select>
                              </div>
                        </div>
                  </div>

                  <div class="col-md-4">
                        <!-- Chi Tiết Hóa Đơn -->
                        <div class="bg-white p-6 rounded-lg shadow-lg mb-8 border border-gray-300">
                              <h3 class="font-semibold text-2xl mb-4">Chi Tiết Hóa Đơn</h3>

                              <div class="mb-4">
                                    <label class="block font-semibold">Nhân Viên</label>
                                    <input type="text" class="form-control"
                                          :value="selectedInvoice?.nhanVien?.hoTen || 'Không có'" readonly />
                              </div>


                              <!-- Hiển thị thông tin khách hàng -->
                              <div class="mb-4">
                                    <label class="block font-semibold">Khách Hàng</label>
                                    <div class="flex items-center">
                                          <!-- Tìm kiếm khách hàng -->
                                          <vue-multiselect v-model="selectedKhachHang" :options="filteredKhachHangList"
                                                option-label="hoTen" option-value="id" placeholder="Chọn khách hàng"
                                                label="Khách Hàng" @search-change="searchCustomers"
                                                @select="updateCustomerInInvoice" :loading="loading"
                                                :custom-label="customLabel" />
                                          <!-- Button để thêm khách hàng mới -->
                                          <button class="btn btn-success1 ml-2" @click="handleAddKhachHang">+</button>
                                    </div>
                              </div>


                              <div class="mb-4">
                                    <label class="block font-semibold">Mã Hóa Đơn</label>
                                    <p class="text-danger">{{ selectedInvoice?.maDonHang || "Chưa chọn" }}</p>
                              </div>


                              <div class="mb-4">
                                    <label class="block font-semibold">Mã Giảm Giá (Nếu Có)</label>
                                    <div class="flex items-center space-x-2">
                                          <!-- Dropdown để chọn voucher -->
                                          <select v-model="selectedVoucher" class="form-control"
                                                @change="applyDiscount">
                                                <option value="" disabled selected>Chọn Voucher</option>
                                                <option v-for="voucher in voucherList" :key="voucher.id"
                                                      :value="voucher">
                                                      {{ voucher.maVoucher }} - {{ voucher.moTa }}%
                                                </option>
                                          </select>

                                    </div>
                                    <div class="mb-4">
                                          <label class="block font-semibold">Phương Thức Thanh Toán</label>

                                          <div class="payment-methods">
                                                <div class="payment-option">
                                                      <input type="radio" name="payment" class="mr-2"
                                                            v-model="paymentMethod" value="cash" />
                                                      <label for="cash">Tiền Mặt</label>
                                                </div>

                                                <div class="payment-option">
                                                      <input type="radio" name="payment" class="ml-4 mr-2"
                                                            v-model="paymentMethod" value="bank-transfer" />
                                                      <label for="bank-transfer">Chuyển Khoản</label>
                                                </div>
                                          </div>
                                    </div>



                                    <!-- Moving the totals section to the bottom -->
                                    <div class="mb-4 mt-8">
                                          <div class="d-flex justify-content-between">
                                                <p class="font-bold">Tổng Tiền (Trước Giảm): </p>
                                                <p class="text-right">{{ totalAmountBeforeDiscount.toLocaleString() }} đ
                                                </p>
                                          </div>
                                          <div class="d-flex justify-content-between">
                                                <p class="font-bold">Giảm Giá:</p>
                                                <p class="text-right text-danger">-{{ discountAmount.toLocaleString() }}
                                                      đ</p>
                                          </div>
                                          <div class="d-flex justify-content-between">
                                                <p class="font-bold text-danger">Tổng Tiền Sau Giảm: </p>
                                                <p class="text-right text-danger">{{
                                                      totalAmountAfterDiscount.toLocaleString() }} đ</p>
                                          </div>
                                    </div>
                              </div>






                              <button class="btn btn-success w-100 mt-4" @click="payInvoice"
                                    :disabled="selectedInvoice.trangThaiDonHang === 'Đã thanh toán'">
                                    Thanh Toán</button>
                        </div>
                        <div v-if="paymentMethod === 'bank-transfer' && showImage"
                              class="col-md-4 flex justify-center items-center">
                              <div id="qr-image-container" class="flex justify-center items-center w-full h-full"
                                    @click="processPayment">
                                    <img src="/src/assets/logo/maQR.jpg" alt="Default QR" />
                                    <!-- Nút quay lại để ẩn mã QR -->
                                    <button class="clo" id="closeQRCode" @click.stop="closeQRCode">X</button>
                              </div>
                        </div>

                  </div>

            </div>

      </div>
</template>

<script>
import { useRouter } from 'vue-router';
import axios from "axios";
import { ref, onMounted, computed, watch } from "vue";
import { jsPDF } from "jspdf";
import { nextTick } from 'vue';
import VueMultiselect from 'vue-multiselect';
import 'vue-multiselect/dist/vue-multiselect.css';
import Swal from 'sweetalert2';
import "jspdf-autotable";
import roboto from '@/assets/font/roboto';

export default {
      components: {
            VueMultiselect
      },
      setup() {
            const userLogin = ref({ name: '' });

            // Khi component được mount, lấy tên người dùng từ localStorage
            onMounted(() => {
                  const user = JSON.parse(localStorage.getItem('user'));

                  console.log("Dữ liệu từ localStorage:", user); // Kiểm tra dữ liệu trong localStorage

                  if (user) {
                        userLogin.value.name = user.tenDangNhap || 'Admin';  // Gán tên đăng nhập hoặc mặc định là 'Admin'
                  } else {
                        userLogin.value.name = 'Admin';  // Nếu không có thông tin người dùng, gán mặc định là 'Admin'
                  }

                  console.log("Tên đăng nhập của nhân viên:", userLogin.value.name);  // Kiểm tra tên đăng nhập
            });
            const user = JSON.parse(localStorage.getItem('user'));
            const nhanVienId = user ? user.id : null;

            const showAmountInput = ref(false);  // Flag to control the visibility of the amount input field
            const voucherList = ref([]);
            const selectedVoucher = ref(null);
            const discountAmount = ref(0);
            const router = useRouter();
            const invoices = ref([]);
            const sanPhamCTList = ref([]);
            const selectedDanhMuc = ref("");
            const selectedThuongHieu = ref("");
            const selectedChatLieu = ref("");
            const selectedDeGiay = ref("");
            const danhMucList = ref([]);

            const thuongHieuList = ref([]);
            const chatLieuList = ref([]);
            const deGiayList = ref([]);
            const selectedKhachHang = ref(null); // Chọn khách hàng khi tạo hóa đơn
            const khachHangList = ref([]);
            const loading = ref(false);
            const errorMessage = ref("");
            const searchQuery = ref("");
            const searchQueryKH = ref("");
            const searchQuerySPCT = ref("");
            const orderDetails = ref([]); // Lưu trữ các sản phẩm trong chi tiết đơn hàng
            const qrGenerated = ref(false);  // Flag to track if QR code has been generated
            const showImage = ref(false);
            const paymentMethod = ref("");  // Khai báo paymentMethod dưới dạng ref
            const savedAmount = ref(0); // Số tiền đã tiết kiệm được từ khuyến mãi

            const discountCode = ref("");
            const pageSize = ref(5);
            const currentPage = ref(1);
            const pageSizeKH = ref(5);
            const currentPageKH = ref(1);
            const pageSizeSPCT = ref(5);
            const currentPageSPCT = ref(1);
            const cartItems = ref([]); // Giỏ hàng
            const khachHangId = ref(2); // ID khách hàng (có thể lấy từ session hoặc từ router)
            const selectedInvoice = ref({});
            const selectedInvoiceCT = ref({});
            const selectedTrangThai = ref(null);
            const closeQRCode = () => {
                  showImage.value = false;  // Ẩn hình ảnh mã QR
                  console.log("Đã đóng mã QR");
            };


            const exportInvoice = () => {
                  const doc = new jsPDF();

                  // Set up font for Vietnamese text
                  // Đăng ký font
                  doc.addFileToVFS('Roboto-Italic.ttf', roboto["Roboto-Italic.ttf"]);
                  doc.addFont('Roboto-Italic.ttf', 'Roboto', 'italic');
                  doc.setFont('Roboto', 'italic');


                  // Header with store information
                  doc.setFontSize(20);
                  doc.text('SPORT SHOES STORE', 105, 20, { align: 'center' });
                  doc.setFontSize(10);
                  doc.text('Địa chỉ: 442 Đội Cấn - Ba Đình - Hà Nội', 105, 30, { align: 'center' });
                  doc.text('Điện thoại: (04) 6674 2332', 105, 35, { align: 'center' });

                  // Invoice information
                  doc.setFontSize(16);
                  doc.text('HÓA ĐƠN THANH TOÁN', 105, 45, { align: 'center' });

                  // Customer and invoice details
                  doc.setFontSize(12);
                  doc.text(`Mã Hóa Đơn: ${selectedInvoice.value.maDonHang}`, 14, 60);
                  doc.text(`Ngày: ${new Date().toLocaleDateString('vi-VN')}`, 14, 65);
                  doc.text(`Khách Hàng: ${selectedInvoice.value.khachHang ? selectedInvoice.value.khachHang.hoTen : 'Khách vãng lai'}`, 14, 70);
                  doc.text(`Nhân Viên: ${selectedInvoice.value.nhanVien ? selectedInvoice.value.nhanVien.hoTen : 'Không có'}`, 14, 75);

                  // Add Voucher Code if exists
                      if (selectedInvoice.value.voucher) {
                          doc.text(`Mã Giảm Giá: ${selectedInvoice.value.voucher.maVoucher} - ${selectedInvoice.value.voucher.moTa}`, 14, 80);
                      }

                  // Product table headers
                  doc.setFontSize(11);
                  doc.text('STT', 14, 90);
                  doc.text('Tên Sản Phẩm', 30, 90);
                  doc.text('Số Lượng', 120, 90);
                  doc.text('Đơn Giá', 150, 90);
                  doc.text('Thành Tiền', 180, 90);

                  // Draw table header line
                  doc.line(14, 92, 196, 92);
                  console.log("Chi tiết đơn hàng:", orderDetails.value);
                  // Product details
                  let yPosition = 100;
                  orderDetails.value.forEach((item, index) => {
                        doc.setFontSize(10);
                        doc.text(`${index + 1}`, 14, yPosition);
                        doc.text(`${item.tenSanPham}`, 30, yPosition); // Đảm bảo field này tồn tại
                        doc.text(`${item.soLuong}`, 120, yPosition);
                        doc.text(`${item.giaBan.toLocaleString()}đ`, 150, yPosition);
                        const thanhTien = item.soLuong * item.giaBan;
                        doc.text(`${thanhTien.toLocaleString()}đ`, 180, yPosition);
                        yPosition += 8;
                  });


                  // Draw final line
                  doc.line(14, yPosition, 196, yPosition);
                  yPosition += 10;

                  // Payment summary
                  doc.setFontSize(11);
                  const tongTienHang = orderDetails.value.reduce((sum, item) => {
                        return sum + (item.soLuong * item.giaBan);
                  }, 0);

                  doc.text(`Tổng tiền hàng: ${tongTienHang.toLocaleString()}đ`, 140, yPosition);

                  yPosition += 8;
                  doc.text(`Giảm giá: ${discountAmount.value.toLocaleString()}đ`, 140, yPosition);
                  yPosition += 8;
                  const tongThanhToan = tongTienHang - discountAmount.value;
                  doc.text(`Tổng thanh toán: ${tongThanhToan.toLocaleString()}đ`, 140, yPosition);

                  yPosition += 15;

                  // Footer selectedInvoice.value.tongTien.toLocaleString()
                  doc.setFontSize(10);
                  doc.text('Cảm ơn quý khách đã mua hàng!', 105, yPosition, { align: 'center' });
                  doc.text('Hẹn gặp lại quý khách!', 105, yPosition + 5, { align: 'center' });

                  // Save the PDF
                  doc.save(`${selectedInvoice.value.maDonHang}.pdf`);
            };





            // Fetch available vouchers from API
            const fetchVouchers = async () => {
                  try {
                        const response = await axios.get("http://localhost:8080/admin/voucher");
                        voucherList.value = response.data.filter(voucher => {
                              const now = new Date();
                              return new Date(voucher.ngayBatDau) <= now && new Date(voucher.ngayKetThuc) >= now;
                        });
                  } catch (error) {
                        console.error("Error fetching vouchers:", error);
                  }
            };
            const selectVoucher = () => {
                  if (selectedVoucher.value) {
                        console.log('Voucher đã chọn:', selectedVoucher.value);
                        applyDiscount(); // Tính toán giảm giá khi chọn voucher
                  }
            };
            const applyDiscount = () => {
                  if (!selectedVoucher.value) {
                        discountAmount.value = 0; // Nếu không chọn voucher, không giảm giá
                        return;
                  }

                  const voucher = selectedVoucher.value; // Voucher đã chọn
                  const totalAmountBeforeDiscount = totalAmount.value;

                  // Log các giá trị trước khi tính toán
                  console.log("Total amount before discount: ", totalAmountBeforeDiscount);
                  console.log("Voucher discount percentage: ", voucher.giaTriGiam);
                  console.log("Voucher discount percentage: ", voucher.loaiVoucher);

                  if (isNaN(totalAmountBeforeDiscount) || totalAmountBeforeDiscount <= 0) {
                        console.error("Tổng tiền không hợp lệ!");
                        discountAmount.value = 0;
                        selectedVoucher.value = null;
                        return;
                  }

                  if (isNaN(voucher.giaTriGiam) || voucher.giaTriGiam < 0) {
                        console.error("Tỷ lệ giảm giá không hợp lệ!");
                        discountAmount.value = 0;
                        selectedVoucher.value = null;
                        return;
                  }
                  if (totalAmountBeforeDiscount < voucher.giaTriToiThieu) {
                        // Nếu tổng tiền nhỏ hơn giá trị tối thiểu của voucher
                        alert(`Giảm giá chỉ áp dụng cho đơn hàng có tổng tiền từ ${voucher.giaTriToiThieu.toLocaleString()} VNĐ.`);
                        discountAmount.value = 0;
                        selectedVoucher.value = null;
                        return;
                  }

                  if (voucher.loaiVoucher === 0) {
                        // Giảm giá theo phần trăm
                        discountAmount.value = (totalAmountBeforeDiscount * voucher.giaTriGiam) / 100;


                  } else {
                        // Giảm giá cố định
                        discountAmount.value = voucher.giaTriGiam;
                  }
            };


            // Total amount before discount
            const totalAmountBeforeDiscount = computed(() => {
                  return orderDetails.value.reduce((total, item) => {
                        const price = item?.giaBan || 0;
                        return total + (price * item.soLuong);
                  }, 0);
            });

            // Total amount after discount
            const totalAmountAfterDiscount = computed(() => {
                  return Math.max(totalAmountBeforeDiscount.value - discountAmount.value, 0);
            });

            // Fetch vouchers on mounted
            onMounted(() => {
                  fetchVouchers();
            });

            const updateCustomerInInvoice = async () => {
                  if (!selectedKhachHang.value) {
                        alert("Vui lòng chọn khách hàng.");
                        return;
                  }

                  const idKhachHang = selectedKhachHang.value ? selectedKhachHang.value.id : null;
                  console.log("Khách hàng đang chọn:", selectedKhachHang.value);

                  // Cập nhật trạng thái đơn hàng thành "Đã thanh toán"
                  try {
                        const response = await axios.put(`http://localhost:8080/don-hang/${selectedInvoice.value.id}`, {
                              khachHang: { id: idKhachHang },  // Cập nhật khách hàng bằng ID (hoặc toàn bộ đối tượng nếu cần)
                        });

                        if (response.status === 200) {
                              alert("✅ Cập nhật đơn hàng thành công.");
                              // Cập nhật thông tin khách hàng vào đối tượng selectedInvoice
                              selectedInvoice.value.khachHang = selectedKhachHang.value;
                              console.log("Thông tin khách hàng đã được cập nhật:", selectedInvoice.value.khachHang);
                              fetchInvoices();
                        } else {
                              alert("❌ Cập nhật không thành công.");
                        }
                  } catch (error) {
                        console.error("❌ Lỗi khi cập nhật khách hàng cho hóa đơn:", error);
                        alert("⚠ Không thể cập nhật khách hàng cho hóa đơn.");
                  }
            };



            // const fetchOrderDetails = async (invoiceId) => {
            //   console.log("🟡 Đang lấy chi tiết đơn hàng cho ID:", invoiceId);

            //   if (!invoiceId) {
            //     console.error("🔴 Lỗi: ID hóa đơn không hợp lệ!");
            //     return;
            //   }

            //   try {
            //     const response = await axios.get(`http://localhost:8080/don-hang-chi-tiet/dh/${invoiceId}`);
            //     console.log("✅ Dữ liệu trả về từ API:", response.data);

            //     if (response.data && Array.isArray(response.data.sanPhamChiTiet)) {
            //       orderDetails.value = response.data.sanPhamChiTiet;
            //     } else {
            //       console.error("🔴 Lỗi: Dữ liệu `sanPhamChiTiet` không hợp lệ!");
            //       orderDetails.value = [];
            //     }
            //   } catch (error) {
            //     console.error("🔴 Lỗi khi lấy chi tiết đơn hàng:", error);
            //     errorMessage.value = "Không thể tải chi tiết đơn hàng.";
            //   } finally {
            //     loading.value = false;
            //   }
            // };
            const filteredInvoices = computed(() => {
                  return invoices.value.filter(invoice => invoice.trangThaiDonHang === "Chờ thanh toán");
            });

            const fetchOrderDetails = async (invoiceId) => {
                  console.log("🔄 Đang lấy chi tiết hóa đơn:", invoiceId);

                  if (!invoiceId) {
                        console.error("⚠ ID hóa đơn không hợp lệ!");
                        return;
                  }

                  try {
                        const response = await axios.get(`http://localhost:8080/don-hang-chi-tiet/${invoiceId}`);
                        console.log("✅ Dữ liệu chi tiết đơn hàng:", response.data);

                        // Kiểm tra dữ liệu và cập nhật danh sách sản phẩm
                        if (response.data && Array.isArray(response.data.sanPhamChiTiet)) {
                              orderDetails.value = response.data.sanPhamChiTiet;
                        } else {
                              console.error("⚠ Dữ liệu không hợp lệ!");
                              orderDetails.value = [];
                        }
                  } catch (error) {
                        console.error("❌ Lỗi khi lấy chi tiết đơn hàng:", error);
                        errorMessage.value = "Không thể tải chi tiết đơn hàng.";
                  }
            };


            // Thêm sản phẩm vào giỏ hàng
            const addProductToInvoice = async (productId, quantity) => {
                  try {
                        const response = await axios.post('http://localhost:8080/don-hang-chi-tiet', {
                              donHang: { id: selectedInvoice.value.id },
                              sanPhamChiTiet: { id: productId },
                              soLuong: quantity,
                        });
                        if (response.status === 200) {
                              fetchOrderDetails(selectedInvoice.value.id);
                              alert("Sản phẩm đã được thêm vào hóa đơn thành công.");
                        }
                  } catch (error) {
                        alert("Không thể thêm sản phẩm vào hóa đơn.");
                  }
            };

            // const addToCart = async (spct) => {
            //   if (!selectedInvoice.value.id) {
            //     alert("⚠ Vui lòng chọn hóa đơn trước khi chọn sản phẩm!");
            //     return;
            //   }

            //   // Kiểm tra trạng thái sản phẩm
            //   if (spct.trangThai === false) {
            //     alert("⚠ Sản phẩm này tạm thời hết hàng!");
            //     return;
            //   }

            //   // Nhập số lượng
            //   const quantity = parseInt(prompt("Nhập số lượng sản phẩm:"), 10);
            //   const maxQuantity = spct.soLuong; // Kiểm tra số lượng sản phẩm có sẵn

            //   if (!quantity || isNaN(quantity) || quantity <= 0) {
            //     alert("⚠ Vui lòng nhập số lượng hợp lệ!");
            //     return;
            //   }

            //   if (quantity > maxQuantity) {
            //     alert(`⚠ Số lượng vượt quá số lượng sản phẩm trong kho! (Tồn kho: ${maxQuantity})`);
            //     return;
            //   }

            //   // Tiến hành thêm sản phẩm vào giỏ hàng
            //   try {
            //     const response = await axios.post("http://localhost:8080/don-hang-chi-tiet", {
            //       donHang: { id: selectedInvoice.value.id },
            //       sanPhamChiTiet: { id: spct.id },
            //       soLuong: quantity
            //     });

            //     if (response.status === 200) {
            //       alert("✅ Sản phẩm đã được thêm vào hóa đơn!");
            //       fetchOrderDetails(selectedInvoice.value.id); // Cập nhật lại chi tiết đơn hàng
            //     } else {
            //       alert("❌ Không thể thêm sản phẩm vào hóa đơn.");
            //     }
            //   } catch (error) {
            //     alert("❌ Lỗi khi thêm sản phẩm vào hóa đơn.");
            //     console.error("Lỗi khi thêm vào HDCT:", error);
            //   }
            // };
            const addToCart = async (spct) => {
                  if (!selectedInvoice.value.id) {
                        alert("⚠ Vui lòng chọn hóa đơn trước khi chọn sản phẩm!");
                        return;
                  }

                  // Kiểm tra trạng thái sản phẩm
                  if (spct.trangThai === false) {
                        alert("⚠ Sản phẩm này tạm thời ngừng bán!");
                        return;
                  }

                  // Nhập số lượng
                  const quantity = parseInt(prompt("Nhập số lượng sản phẩm:"), 10);
                  const maxQuantity = spct.soLuong; // Kiểm tra số lượng sản phẩm có sẵn

                  if (!quantity || isNaN(quantity) || quantity <= 0) {
                        alert("⚠ Vui lòng nhập số lượng hợp lệ!");
                        return;
                  }

                  if (quantity > maxQuantity) {
                        alert(`⚠ Số lượng vượt quá số lượng sản phẩm trong kho! (Tồn kho: ${maxQuantity})`);
                        return;
                  }

                  // Tiến hành thêm sản phẩm vào giỏ hàng
                  try {
                        const response = await axios.post("http://localhost:8080/don-hang-chi-tiet", {
                              donHang: { id: selectedInvoice.value.id },
                              sanPhamChiTiet: { id: spct.id },
                              soLuong: quantity,
                        });

                        if (response.status === 200) {
                              alert("✅ Sản phẩm đã được thêm vào hóa đơn!");


        console.log("👉 Bắt đầu fetchOrderDetails()");
        await fetchOrderDetails(selectedInvoice.value.id);

        console.log("👉 Bắt đầu calculateTotalAmount()");
        const updatedTotalAmount = await calculateTotalAmount(selectedInvoice.value.id);

        console.log("👉 Cập nhật tổng tiền vào đơn hàng");
        selectedInvoice.value.tongTien = updatedTotalAmount;

                              // Gửi yêu cầu PUT để cập nhật đơn hàng trên server
                              await axios.put(`http://localhost:8080/don-hang/${selectedInvoice.value.id}`, {
                                    tongTien: updatedTotalAmount,  // Cập nhật tổng tiền mới
                              });

                              // Cập nhật lại danh sách hóa đơn mà không cần reload trang
                              const invoiceIndex = invoices.value.findIndex(invoice => invoice.id === selectedInvoice.value.id);
                              if (invoiceIndex !== -1) {
                                    invoices.value[invoiceIndex] = { ...selectedInvoice.value }; // Cập nhật hóa đơn trong danh sách
                              }

                              // Sử dụng nextTick để đảm bảo Vue tái render sau khi cập nhật dữ liệu
                              nextTick(() => {
                                    // Tái render giao diện
                              });

                        } else {
                        console.error("Lỗi khi thêm sản phẩm: ", response.data);
                              alert("❌ Không thể thêm sản phẩm vào hóa đơn.");
                        }
                  } catch (error) {
                         console.error("Lỗi khi thêm vào giỏ hàng:", error);
                            if (error.response && error.response.data) {
                                console.error("Thông tin lỗi từ server:", error.response.data);
                            }
                            alert("❌ Số lượng sản phẩm vượt quá số lượng tồn kho!");
                  }
            };





            // const getDiscountFromSPCT = (orderDetails) => {
            //   // Kiểm tra nếu orderDetails không rỗng
            //   if (orderDetails && orderDetails.length > 0) {
            //     // Tìm sản phẩm có khuyến mãi lớn nhất
            //     const maxDiscountProduct = orderDetails.reduce((max, spct) => {
            //       // So sánh phần trăm giảm giá của sản phẩm
            //       if (spct.phanTramGiamGia > max.phanTramGiamGia) {
            //         return spct;  // Trả về sản phẩm có khuyến mãi lớn nhất
            //       }
            //       return max;
            //     }, { phanTramGiamGia: 0 });  // Giá trị khởi tạo với phần trăm giảm giá là 0

            //     const discountName = maxDiscountProduct.tenKhuyenMai || 'Không có';  // Tên mã giảm giá
            //     const discountValue = maxDiscountProduct.phanTramGiamGia || 0;  // Phần trăm giảm giá

            //     return { discountName, discountValue };
            //   }

            //   return { discountName: 'Không có', discountValue: 0 };  // Nếu không có sản phẩm, trả về giá trị mặc định
            // };
            const getDiscountFromSPCT = (orderDetails) => {
                  // Kiểm tra nếu orderDetails không rỗng
                  if (orderDetails && orderDetails.length > 0) {
                        // Tìm sản phẩm có khuyến mãi lớn nhất và có trạng thái khuyến mãi là true (hoạt động)
                        const validDiscountProduct = orderDetails.reduce((max, spct) => {
                              // Kiểm tra nếu khuyến mãi của sản phẩm là true (hoạt động) và phần trăm giảm giá của sản phẩm lớn hơn sản phẩm trước đó
                              if (spct.trangThai === true && spct.phanTramGiamGia > max.phanTramGiamGia) {
                                    return spct;  // Trả về sản phẩm có khuyến mãi hợp lệ và phần trăm giảm giá lớn nhất
                              }
                              return max;
                        }, { phanTramGiamGia: 0, trangThai: false });  // Khởi tạo với trạng thái khuyến mãi false (không hợp lệ)

                        // Kiểm tra xem có sản phẩm hợp lệ với khuyến mãi không
                        if (validDiscountProduct.trangThai === true) {
                              const discountName = validDiscountProduct.tenKhuyenMai || 'Không có';
                              const discountValue = validDiscountProduct.phanTramGiamGia || 0;
                              return { discountName, discountValue, valid: true };  // Trả về khuyến mãi hợp lệ
                        }

                        // Nếu không có sản phẩm hợp lệ, tìm sản phẩm với khuyến mãi hợp lệ nhưng có phần trăm giảm giá thấp hơn
                        const fallbackDiscountProduct = orderDetails.find(spct => spct.trangThai === true && spct.phanTramGiamGia > 0);
                        if (fallbackDiscountProduct) {
                              return {
                                    discountName: fallbackDiscountProduct.tenKhuyenMai || 'Không có',
                                    discountValue: fallbackDiscountProduct.phanTramGiamGia,
                                    valid: true
                              };
                        }
                  }

                  return { discountName: 'Không có', discountValue: 0, valid: false };  // Trả về "Không có" nếu không có khuyến mãi hợp lệ
            };



            const fetchGioHang = async () => {
                  loading.value = true;
                  errorMessage.value = "";

                  try {
                        const response = await axios.get(`http://localhost:8080/gio-hang/${khachHangId.value}`);
                        if (response.data && Array.isArray(response.data)) {
                              cartItems.value = response.data; // Gán dữ liệu giỏ hàng
                        } else {
                              throw new Error("Dữ liệu không hợp lệ");
                        }
                  } catch (error) {
                        console.error("Lỗi khi tải dữ liệu giỏ hàng:", error);
                        errorMessage.value = "Lỗi khi tải dữ liệu giỏ hàng.";
                  } finally {
                        loading.value = false;
                  }
            };

            // Xóa sản phẩm khỏi giỏ hàng
            const removeFromCart = async (id) => {
                  const confirmation = confirm("Bạn có chắc chắn muốn xóa sản phẩm khỏi giỏ hàng?");
                  if (!confirmation) {
                        return;  // Nếu người dùng không xác nhận, dừng hành động
                  }
                  try {
                        await axios.delete(`http://localhost:8080/gio-hang/remove/${id}`);
                        fetchOrderDetails(); // Sau khi xóa, tải lại giỏ hàng
                  } catch (error) {
                        console.error("Lỗi khi xóa sản phẩm:", error);
                        errorMessage.value = "Lỗi khi xóa sản phẩm khỏi giỏ hàng.";
                  }
            };
            // const updateQuantity = async (sanPhamChiTiet) => {
            //     // Kiểm tra xem sanPhamChiTiet có giá trị hay không
            //     if (!sanPhamChiTiet || !sanPhamChiTiet.id) {
            //         console.error("❌ Không tìm thấy sản phẩm chi tiết hoặc ID không hợp lệ!");
            //         alert("❌ Không tìm thấy sản phẩm chi tiết hoặc ID không hợp lệ!");
            //         return;
            //     }

            //     if (sanPhamChiTiet.soLuong <= 0 || isNaN(sanPhamChiTiet.soLuong)) {
            //         alert("Số lượng phải là số dương hợp lệ.");
            //         return;
            //     }

            //     // Tạo đối tượng dữ liệu cập nhật với thông tin của sản phẩm chi tiết
            //     const updatedItem = {
            //         sanPhamChiTiet: { id: sanPhamChiTiet.id },  // ID của sản phẩm chi tiết
            //         soLuong: sanPhamChiTiet.soLuong  // Cập nhật số lượng của sản phẩm chi tiết
            //     };

            //     try {
            //         // Gửi PUT request để cập nhật số lượng sản phẩm chi tiết
            //         const response = await axios.put(`http://localhost:8080/don-hang-chi-tiet/update/${sanPhamChiTiet.id}`, updatedItem);

            //         if (response.status === 200) {
            //             alert("Số lượng sản phẩm chi tiết đã được cập nhật thành công.");
            //             fetchOrderDetails(selectedInvoice.value.id);  // Cập nhật lại chi tiết đơn hàng
            //         } else {
            //             alert("❌ Cập nhật số lượng không thành công.");
            //         }
            //     } catch (error) {
            //         console.error("❌ Lỗi khi cập nhật số lượng:", error);
            //         alert("❌ Không thể cập nhật số lượng.");
            //     }
            // };
            const fetchProductStock = async (productId) => {
                  try {
                        const response = await axios.get(`http://localhost:8080/san-pham-chi-tiet/${productId}`);
                        return response.data.soLuong || 0; // Trả về số lượng tồn kho hoặc 0 nếu không có
                  } catch (error) {
                        console.error("Lỗi khi lấy số lượng tồn kho:", error);
                        return 0;  // Trả về 0 nếu có lỗi
                  }
            };

            const updateQuantity = async (sanPhamChiTiet) => {
                  if (!sanPhamChiTiet || !sanPhamChiTiet.idspct) {
                        console.error("❌ Không tìm thấy sản phẩm chi tiết hoặc ID không hợp lệ!");
                        alert("❌ Không tìm thấy sản phẩm chi tiết hoặc ID không hợp lệ!");
                        return;
                  }

                  if (sanPhamChiTiet.soLuong <= 0 || isNaN(sanPhamChiTiet.soLuong)) {
                        fetchOrderDetails(selectedInvoice.value.id);
                        alert("Số lượng phải là số dương hợp lệ.");

                        return;
                  }

                  // Lấy số lượng tồn kho từ API
                  const maxQuantity = await fetchProductStock(sanPhamChiTiet.idspct);

                  // Kiểm tra nếu số lượng nhập vào vượt quá số lượng tồn kho
                  if (sanPhamChiTiet.soLuong > maxQuantity) {
                        alert(`⚠ Số lượng nhập vào vượt quá số lượng tồn kho! (Tồn kho: ${maxQuantity})`);
                        sanPhamChiTiet.soLuong = maxQuantity; // Set lại số lượng về tối đa trong kho
                        return;
                  }

                  // Tạo đối tượng dữ liệu cập nhật với thông tin của sản phẩm chi tiết
                  const updatedItem = {
                        sanPhamChiTiet: { id: sanPhamChiTiet.idspct }, // Sử dụng idspct thay vì id
                        soLuong: sanPhamChiTiet.soLuong
                  };

                  try {
                        // Gửi PUT request để cập nhật số lượng sản phẩm chi tiết
                        const response = await axios.put(`http://localhost:8080/don-hang-chi-tiet/update/${sanPhamChiTiet.id}`, updatedItem);

                        if (response.status === 200) {
                              alert("Số lượng sản phẩm đã được cập nhật thành công.");
                              fetchOrderDetails(selectedInvoice.value.id);  // Cập nhật lại chi tiết đơn hàng
                              // Cập nhật tổng tiền sau khi thay đổi số lượng
                              const updatedTotalAmount = await calculateTotalAmount(selectedInvoice.value.id);

                              // Cập nhật tổng tiền trong selectedInvoice
                              selectedInvoice.value.tongTien = updatedTotalAmount;

                              // Cập nhật lại danh sách hóa đơn mà không cần reload trang
                              const invoiceIndex = invoices.value.findIndex(invoice => invoice.id === selectedInvoice.value.id);
                              if (invoiceIndex !== -1) {
                                    invoices.value[invoiceIndex] = { ...selectedInvoice.value }; // Cập nhật hóa đơn trong danh sách
                              }

                              // Sử dụng nextTick để đảm bảo Vue tái render sau khi cập nhật dữ liệu
                              nextTick(() => {
                                    // Tái render giao diện
                              });
                        } else {
                              alert("❌ Cập nhật số lượng không thành công.");
                        }
                  } catch (error) {
                        console.error("❌ Lỗi khi cập nhật số lượng:", error);
                        alert("❌ Không thể cập nhật số lượng.");
                  }
            };

            const calculateTotalAmount = async (invoiceId) => {
                  try {
                        const response = await axios.get(`http://localhost:8080/don-hang-chi-tiet/${invoiceId}`);
                        const orderDetails = response.data.sanPhamChiTiet;  // Dữ liệu sản phẩm chi tiết trong hóa đơn

                        if (!Array.isArray(orderDetails)) {
                              console.error("Dữ liệu 'sanPhamChiTiet' không phải là mảng:", orderDetails);
                              return 0;
                        }

                        // Tính tổng tiền
                        const totalAmount = orderDetails.reduce((total, item) => {
                              const price = item.giaBan || 0;
                              const quantity = item.soLuong || 0;
                              return total + (price * quantity);
                        }, 0);

                        return totalAmount;  // Trả về tổng tiền tính được
                  } catch (error) {
                        // In thêm thông tin lỗi chi tiết từ server nếu có
                              if (error.response) {
                                  console.error("Lỗi từ server:", error.response.data);
                                  console.error("Mã lỗi:", error.response.status);
                              } else {
                                  console.error("Lỗi khi tính toán tổng tiền:", error.message);
                              }
                              return 0;  // Nếu có lỗi, trả về 0
                  }
            };









            const removeFromOrder = async (orderDetailId) => {
                  // Xác nhận trước khi xóa
                  const confirmation = confirm("Bạn có chắc chắn muốn xóa sản phẩm này không?");
                  if (!confirmation) {
                        return;  // Nếu người dùng không xác nhận, dừng hành động
                  }

                  if (!orderDetailId) {
                        alert("❌ Vui lòng chọn chi tiết đơn hàng trước khi xóa.");
                        return;
                  }

                  try {
                        // Gửi yêu cầu xóa chi tiết đơn hàng (don_hang_chi_tiet) dựa trên orderDetailId
                        const responseDeleteDetails = await axios.delete(`http://localhost:8080/don-hang-chi-tiet/${orderDetailId}`);
                        console.log(`Response Status for deleting order details: ${responseDeleteDetails.status}`);

                        if (responseDeleteDetails.status === 200) {
                              alert("✅ Xóa chi tiết đơn hàng thành công.");
                              await fetchOrderDetails(selectedInvoice.value.id);
                              const updatedOrderDetails = orderDetails.value.filter(item => item.id !== orderDetailId); // Lọc bỏ item đã xóa
                              orderDetails.value = updatedOrderDetails;  // Cập nhật lại state với dữ liệu đã lọc
                              // Tính lại tổng tiền sau khi xóa chi tiết đơn hàng
                              const updatedTotalAmount = updatedOrderDetails.reduce((total, item) => {
                                    const giaBan = item?.giaBan || 0;
                                    const soLuong = item?.soLuong || 0;
                                    return total + (giaBan * soLuong);
                              }, 0);

                              // Cập nhật tổng tiền vào hóa đơn (gửi API để cập nhật)
                              const updatedInvoiceData = {
                                    tongTien: updatedTotalAmount
                              };
                              const updateInvoiceResponse = await axios.put(`http://localhost:8080/don-hang/${selectedInvoice.value.id}`, updatedInvoiceData);


                        } else {
                              alert("❌ Xóa chi tiết đơn hàng không thành công.");
                              return;
                        }
                        fetchInvoices();

                        // Nếu xóa thành công, tải lại chi tiết đơn hàng
                        // Cập nhật lại danh sách chi tiết đơn hàng
                  } catch (error) {
                        console.error("❌ Lỗi khi xóa chi tiết đơn hàng:", error);
                        alert("⚠ Không thể xóa chi tiết đơn hàng. Vui lòng thử lại.");
                  }
            };



            const selectOrderDetail = (orderDetail) => {
                  selectedInvoiceCT.value = orderDetail; // Lưu chi tiết đơn hàng vào selectedInvoiceCT
            };







            const updateQuantityInOrder = async (item) => {
                  try {
                        const updatedItem = {
                              sanPhamChiTiet: { id: item.sanPhamChiTiet.id },  // Send the product detail ID
                              soLuong: item.soLuong  // Send the updated quantity
                        };

                        const response = await axios.put(`http://localhost:8080/don-hang/${selectedInvoice.value.id}/update-quantity`, updatedItem);

                        if (response.status === 200) {
                              alert("Số lượng sản phẩm đã được cập nhật thành công.");
                              fetchOrderDetails(selectedInvoice.value.id);  // Refresh the order details
                        }
                  } catch (error) {
                        alert("Không thể cập nhật số lượng.");
                  }
            };


            // Tính tổng tiền giỏ hàng
            const totalAmount = computed(() => {
                  return orderDetails.value.reduce((total, item) => {
                        const giaBan = item?.giaBan || item?.sanPhamChiTiet?.giaBan || 0;
                        const soLuong = item?.soLuong || 0;
                        return total + (giaBan * soLuong);
                  }, 0);
            });

            const amountPaid = ref("");  // Số tiền khách đưa
            const isAmountInvalid = ref(false);  // Biến để kiểm tra số tiền hợp lệ






            // Fetch danh sách hóa đơn từ API
            const fetchInvoices = async () => {
                  try {
                        const response = await axios.get("http://localhost:8080/don-hang");
                        invoices.value = response.data;
                  } catch (error) {
                        console.error("Lỗi khi tải hóa đơn:", error);
                        errorMessage.value = "Không thể tải danh sách hóa đơn.";
                  }
            };
            const paginatedInvoices = computed(() => {
                  const startIndex = (currentPageInvoice.value - 1) * pageSizeInvoice.value;
                  return filteredInvoices.value.slice(startIndex, startIndex + pageSizeInvoice.value);
            });

            const selectInvoice = async (invoice) => {
                  console.log("Hóa đơn được chọn:", invoice); // Kiểm tra xem invoice có hợp lệ không

                  if (!invoice || !invoice.id) {
                        console.error("Invoice ID không hợp lệ");
                        alert("Hóa đơn không hợp lệ, không có ID.");
                        return;  // Nếu không có ID thì thoát
                  }

                  selectedInvoice.value = { ...invoice };  // Cập nhật hóa đơn được chọn
                  orderDetails.value = [];
                  // Nếu hóa đơn có khách hàng, gán thông tin khách hàng vào selectedKhachHang
                  if (invoice.khachHang) {
                        selectedKhachHang.value = invoice.khachHang;  // Gán khách hàng từ hóa đơn vào selectedKhachHang
                  } else {
                        selectedKhachHang.value = null;  // Nếu không có khách hàng, gán null
                  }
                  // Kiểm tra xem hóa đơn có nhân viên không
                  console.log("Nhân viên trong hóa đơn:", invoice.nhanVien);
                  await fetchOrderDetails(invoice.id); // Lấy chi tiết đơn hàng từ API
            };



            // watch(orderDetails, (newVal) => {
            //      console.log("Cập nhật orderDetails: ", newVal);
            // });
            // watch(totalAmountBeforeDiscount, async (newTotal) => {
            //      // Kiểm tra nếu đã chọn hóa đơn và tổng tiền đã thay đổi
            //       if (selectedInvoice.value && newTotal !== selectedInvoice.value.tongTien) {
            //             // Cập nhật lại tổng tiền trong hóa đơn
            //             try {
            //                   await axios.put(`http://localhost:8080/don-hang/${selectedInvoice.value.id}`, {
            //                          tongTien: newTotal,  // Cập nhật tổng tiền sau giảm
            //                    });
            //
            //                    console.log(`Đã cập nhật tổng tiền thành ${newTotal} cho hóa đơn ${selectedInvoice.value.maDonHang}`);
            //             } catch (error) {
            //                   console.error("Lỗi khi cập nhật tổng tiền:", error);
            //              }
            //         }
            //  });
            const getInvoiceById = async (id) => {
                  const response = await axios.get(`http://localhost:8080/don-hang/${id}`);
                  return response.data;
            };


            const processPayment = async () => {
                  // Ẩn hình ảnh QR code khi người dùng click vào
                  showImage.value = false;
                  // Kiểm tra số lượng tồn kho trước khi thanh toán
                  let insufficientStockItems = [];
                  for (let item of orderDetails.value) {
                        // Lấy số lượng tồn kho cho mỗi sản phẩm từ API
                        const stockQuantity = await fetchProductStock(item.idspct); // Giả sử fetchProductStock trả về số lượng tồn kho

                        if (item.soLuong > stockQuantity) {
                              insufficientStockItems.push({
                                    itemName: item.tenSanPham, // Giả sử 'tenSanPham' là tên sản phẩm
                                    orderedQuantity: item.soLuong, // Số lượng yêu cầu
                                    availableStock: stockQuantity, // Số lượng tồn kho
                              });
                        }
                  }

                  // Nếu có sản phẩm không đủ tồn kho, hiển thị thông báo và dừng tiến trình thanh toán
                  if (insufficientStockItems.length > 0) {
                        let message = "Số lượng trong đơn hàng vượt quá số lượng tồn kho:\n\n";
                        insufficientStockItems.forEach(item => {
                              message += `<strong>  Sản phẩm:</strong> ${item.itemName}<br><strong>Số lượng mua:</strong> ${item.orderedQuantity}<br><strong>Tồn kho:</strong> ${item.availableStock}<br><br>`;
                        });

                        // Hiển thị thông báo đẹp với SweetAlert2
                        Swal.fire({
                              title: 'Thông báo lỗi!',
                              html: message,
                              icon: 'error',
                              confirmButtonText: 'Đóng',
                              customClass: {
                                    title: 'swal-title',
                                    content: 'swal-content',
                                    confirmButton: 'swal-button'
                              },
                              buttonsStyling: false,
                        });

                        return;
                  }

                  // Tiến hành thanh toán nếu điều kiện đã được thỏa mãn
                  try {
                        // Cập nhật số lượng sản phẩm chi tiết và trạng thái sau khi thanh toán
                        for (let item of orderDetails.value) {
                              const soLuongThanhToan = item.soLuongThanhToan || 0;  // Số lượng thanh toán được nhập từ người dùng hoặc tính toán từ giỏ hàng
                              const updatedQuantity = item.soLuong - soLuongThanhToan;  // Giảm số lượng theo số lượng đã thanh toán

                              await axios.put(`http://localhost:8080/san-pham-chi-tiet/update/${item.idspct}`, {
                                    soLuong: updatedQuantity,  // Cập nhật số lượng sau khi thanh toán
                                    size: item.size,           // Giữ nguyên size
                                    sanPham: item.sanPham,     // Giữ nguyên sản phẩm
                                    khuyenMai: item.khuyenMai, // Giữ nguyên khuyến mãi
                                    giaBan: item.giaBan,       // Giữ nguyên giá bán
                                    maSPCT: item.maSPCT,       // Giữ nguyên mã sản phẩm chi tiết
                                    mauSac: item.mauSac,       // Giữ nguyên màu sắc
                                    trangThai: item.trangThai  // Giữ nguyên trạng thái
                              });
                        }

                        // Kiểm tra nếu khách hàng đã được chọn
                        const idKhachHang = selectedKhachHang.value ? selectedKhachHang.value.id : null;
                        console.log(selectedInvoice.value.khachHang);
                        const idVoucher = selectedVoucher.value ? selectedVoucher.value.id : null;
                        // Cập nhật trạng thái đơn hàng thành "Đã thanh toán"
                        await axios.put(`http://localhost:8080/don-hang/${selectedInvoice.value.id}`, {
                              trangThaiDonHang: 'Đã thanh toán',
                              khachHang: { id: idKhachHang },  // Cập nhật id khách hàng
                              tongTien: totalAmountAfterDiscount.value, // Cập nhật tổng tiền sau giảm
                              voucher: { id: idVoucher },
                              phuongThucThanhToan: { id: paymentMethod.value === 'cash' ? 1 : 2 }, // 2 cho chuyển khoản, 1 cho tiền mặt
                              nhanVien: { id: nhanVienId }
                        });

                        console.log({
                              trangThaiDonHang: 'Đã thanh toán',
                              idKhachHang: idKhachHang,
                              voucher: idVoucher,
                              tongTien: totalAmountAfterDiscount.value,
                              phuongThucThanhToan: paymentMethod.value === 'cash' ? 1 : 2,
                              nhanVienId: nhanVienId  // Thông tin nhân viên
                        });

                        // Giảm số lượng voucher đã sử dụng
                        if (selectedVoucher.value) {
                              const voucherId = selectedVoucher.value.id;

                              // Lấy dữ liệu voucher hiện tại để giữ nguyên các trường còn lại
                              const voucherResponse = await axios.get(`http://localhost:8080/admin/voucher/${voucherId}`);
                              const voucherData = voucherResponse.data;

                              // Giảm số lượng voucher đi 1 và giữ nguyên các trường khác
                              const updatedVoucher = {
                                    ...voucherData,
                                    soLuong: voucherData.soLuong - 1,  // Giảm số lượng
                                    trangThai: voucherData.soLuong - 1 <= 0 ? false : voucherData.trangThai
                              };

                              // Cập nhật lại số lượng voucher đã sử dụng mà không thay đổi các trường khác
                              await axios.put(`http://localhost:8080/admin/voucher/${voucherId}`, updatedVoucher);

                              console.log(`Voucher ${selectedVoucher.value.maVoucher} đã được giảm số lượng.`);
                        }

                        Swal.fire({
                              title: 'Thanh toán thành công!',
                              text: 'Hóa đơn của bạn đã được thanh toán.',
                              icon: 'success',
                              confirmButtonText: 'Đóng',
                              customClass: {
                                    title: 'swal-title',
                                    content: 'swal-content',
                                    confirmButton: 'swal-button'
                              },
                              buttonsStyling: false,
                        });
                        // Sau khi cập nhật đơn hàng và sản phẩm
                        await fetchInvoices(); // Tải lại danh sách hóa đơn
                        selectedInvoice.value = await getInvoiceById(selectedInvoice.value.id); // Lấy lại hóa đơn đã được cập nhật đầy đủ

                        exportInvoice(); // Bây giờ gọi sẽ có đủ dữ liệu

                        orderDetails.value = [];
                        fetchSanPhamChiTiet();
                        // Xóa các chi tiết đơn hàng và tải lại dữ liệu
                        amountPaid.value = "";
                        selectedVoucher.value = null;
                        discountAmount.value = 0;


                  } catch (error) {
                        console.error("Lỗi khi thanh toán:", error);
                        errorMessage.value = "Lỗi khi thanh toán.";
                  }
            };




            const payInvoice = async () => {
                  if (!selectedInvoice.value.id) {
                        alert("Chưa chọn hóa đơn.");
                        return;
                  }

                  // Xác nhận thanh toán
                  const confirmation = confirm("Bạn có chắc chắn muốn thanh toán hóa đơn này?");
                  if (!confirmation) {
                        return;  // Nếu người dùng không xác nhận, dừng hành động
                  }


                  // Kiểm tra xem người dùng đã chọn phương thức thanh toán chưa
                  if (!paymentMethod.value) {
                        alert("⚠ Vui lòng chọn phương thức thanh toán.");
                        return;
                  }
                  const parsedAmount = parseFloat(amountPaid.value.replace(/,/g, "")); // Remove commas if any


                  // Xử lý Thanh Toán Tiền Mặt
                  if (paymentMethod.value === 'cash') {
                        showAmountInput.value = true;

                        await processPayment(); // Nếu thanh toán bằng tiền mặt, trực tiếp tiến hành thanh toán

                  }

                  // Xử lý Chuyển Khoản Ngân Hàng
                  if (paymentMethod.value === 'bank-transfer') {
                        showImage.value = true;  // Hiển thị hình ảnh QR code yêu cầu bấm vào
                  }


            };


            // const payInvoice = async () => {
            //   if (!selectedInvoice.value.id) {
            //     alert("Chưa chọn hóa đơn.");
            //     return;
            //   }

            //   // Xử lý Thanh Toán Tiền Mặt
            //   if (paymentMethod.value === 'cash') {
            //     showAmountInput.value = true;
            //     if (amountPaid.value < totalAmount.value) {
            //       alert("Số tiền khách đưa không đủ để thanh toán!");
            //       return;
            //     }
            //   }

            //   // Xử lý Chuyển Khoản Ngân Hàng
            //   if (paymentMethod.value === 'bank-transfer') {
            //     showQRCode.value = true;  // Hiển thị QR code

            //     // Tạo QR code động (nếu cần)
            //     const qrData = `Thanh toán: ${totalAmount.value.toLocaleString()} đ`;
            //     QRCode.toCanvas(this.$refs.qrcode, qrData, function (error) {
            //       if (error) {
            //         console.error(error);
            //       }
            //     });
            //   }

            //   // Tiến hành thanh toán nếu điều kiện đã được thỏa mãn
            //   try {
            //     // 1. Cập nhật số lượng sản phẩm chi tiết và trạng thái sau khi thanh toán
            //     for (let item of orderDetails.value) {
            //       // Tính toán số lượng thanh toán, bạn có thể tính nó từ dữ liệu bạn có
            //       const soLuongThanhToan = item.soLuongThanhToan || 0;  // Số lượng thanh toán được nhập từ người dùng hoặc tính toán từ giỏ hàng
            //       const updatedQuantity = item.soLuong - soLuongThanhToan;  // Giảm số lượng theo số lượng đã thanh toán

            //       // Cập nhật số lượng và trạng thái trong chi tiết sản phẩm
            //       await axios.put(`http://localhost:8080/san-pham-chi-tiet/${item.idspct}`, {
            //         soLuong: updatedQuantity,  // Cập nhật số lượng sau khi thanh toán
            //         size: item.size,           // Giữ nguyên size
            //         sanPham: item.sanPham,     // Giữ nguyên sản phẩm
            //         khuyenMai: item.khuyenMai, // Giữ nguyên khuyến mãi
            //         giaBan: item.giaBan,       // Giữ nguyên giá bán
            //         maSPCT: item.maSPCT,       // Giữ nguyên mã sản phẩm chi tiết
            //         mauSac: item.mauSac,       // Giữ nguyên màu sắc
            //         trangThai: updatedQuantity === 0 ? false : item.trangThai // Nếu số lượng = 0, thay đổi trạng thái thành false
            //       });
            //     }

            //     // 2. Cập nhật trạng thái đơn hàng thành "Đã thanh toán"
            //     await axios.put(`http://localhost:8080/don-hang/${selectedInvoice.value.id}`, {
            //       trangThaiDonHang: 'Đã thanh toán'
            //     });

            //     alert("Thanh toán thành công!");
            //     fetchInvoices();  // Tải lại danh sách hóa đơn để phản ánh thay đổi
            //     fetchSanPhamChiTiet();
            //   } catch (error) {
            //     console.error("Lỗi khi thanh toán:", error);
            //     errorMessage.value = "Lỗi khi thanh toán.";
            //   }
            // };






            // const selectInvoice = async (invoice) => {
            //   console.log("Hóa đơn được chọn:", invoice);
            //   selectedInvoice.value = { ...invoice };

            //   // Gọi API để lấy các sản phẩm trong giỏ hàng của hóa đơn này
            //   try {
            //     const response = await axios.get(`http://localhost:8080/gio-hang/${invoice.id}`); // Giả sử `invoice.id` là khóa để lấy giỏ hàng
            //     cartItems.value = response.data || []; // Cập nhật danh sách sản phẩm trong giỏ hàng
            //   } catch (error) {
            //     console.error("Lỗi khi tải sản phẩm giỏ hàng:", error);
            //     errorMessage.value = "Không thể tải sản phẩm trong giỏ hàng cho hóa đơn này.";
            //   }
            // };


            const createInvoice = async () => {
                  // Xác nhận tạo hóa đơn
                  const confirmation = confirm("Bạn có chắc chắn muốn tạo hóa đơn mới?");
                  if (!confirmation) {
                        return;  // Nếu người dùng không xác nhận, dừng hành động
                  }
                  try {
                        const invoiceData = {
                              maDonHang: `HD${Date.now()}`, // 🆕 Tạo mã đơn hàng tự động
                              khachHang: null, // ✅ Không cần chọn khách hàng
                              nhanVien: { id: nhanVienId }, // Giả định nhân viên có ID = 1
                              tongTien: 0, // Đảm bảo tổng tiền hợp lệ
                              chiPhiGiaoHang: 0, // 🛠 Fix lỗi bằng cách đặt giá trị mặc định
                              loaiDonHang: 0, // 🏠 Offline
                              trangThaiDonHang: "Chờ thanh toán",
                              ngayTao: new Date().toISOString(),
                              ngaySua: new Date().toISOString(),
                              phuongThucThanhToan: null, // Giả định có ID 1 cho phương thức thanh toán
                              voucher: null, // Có thể cập nhật sau nếu cần
                        };

                        console.log("🛠 Debug Invoice Data:", JSON.stringify(invoiceData, null, 2));

                        const response = await axios.post("http://localhost:8080/don-hang/create", invoiceData);
                        fetchInvoices(); // Load lại danh sách hóa đơn

                        alert("🎉 Hóa đơn mới đã được tạo thành công!");
                  } catch (error) {
                        console.error("❌ Lỗi khi tạo hóa đơn:", error);

                        if (error.response) {
                              console.log("💡 Response Data:", error.response.data);
                              alert(`🚨 Lỗi từ server: ${error.response.data.message || "Không xác định"}`);
                        }

                        errorMessage.value = "Không thể tạo hóa đơn. Vui lòng thử lại!";
                  }
            };

            const visiblePagesInvoice = computed(() => {
                  const maxPagesToShow = 5;
                  const total = Math.ceil(invoices.value.length / pageSizeInvoice.value);
                  const current = currentPageInvoice.value;

                  let start = Math.max(current - 2, 1);
                  let end = Math.min(current + 2, total);

                  if (start === 1) {
                        end = Math.min(start + maxPagesToShow - 1, total);
                  } else if (end === total) {
                        start = Math.max(total - maxPagesToShow + 1, 1);
                  }

                  return Array.from({ length: end - start + 1 }, (_, i) => start + i);
            });
            const currentPageInvoice = ref(1);
            const pageSizeInvoice = ref(5);
            const totalPagesInvoice = computed(() => {
                  return Math.ceil(filteredInvoices.value.length / pageSizeInvoice.value);
            });


            const nextPageInvoice = () => {
                  if (currentPageInvoice.value < totalPagesInvoice.value) {
                        currentPageInvoice.value++;
                  }
            };

            const prevPageInvoice = () => {
                  if (currentPageInvoice.value > 1) {
                        currentPageInvoice.value--;
                  }
            };

            const changePageInvoice = (page) => {
                  currentPageInvoice.value = page;
            };

            //xoa hoa don
            const cancelInvoice = async () => {


                  // Xác nhận trước khi hủy
                  const confirmation = confirm("Bạn có chắc chắn muốn hủy hóa đơn này?");
                  if (!confirmation) {
                        return;  // Nếu người dùng không xác nhận, dừng hành động
                  }

                  // Kiểm tra nếu đơn hàng có sản phẩm
                  if (orderDetails.value.length > 0) {
                        try {
                              // Cập nhật trạng thái đơn hàng thành "Đã hủy" nếu có sản phẩm
                              const response = await axios.put(`http://localhost:8080/don-hang/${selectedInvoice.value.id}`, {
                                    trangThaiDonHang: 'Đã hủy',
                              });

                              if (response.status === 200) {
                                    alert("✅ Hóa đơn đã được hủy thành công!");
                                    orderDetails.value = []; // Xóa chi tiết đơn hàng sau khi hủy
                                    fetchInvoices(); // Tải lại danh sách hóa đơn
                                    selectedInvoice.value = {}; // Reset hóa đơn đã chọn
                              } else {
                                    alert("❌ Không thể hủy hóa đơn.");
                              }
                        } catch (error) {
                              console.error("❌ Lỗi khi hủy hóa đơn:", error);
                              alert("⚠ Không thể hủy hóa đơn. Vui lòng thử lại!");
                        }
                  } else {
                        try {
                              // Xóa đơn hàng nếu không có sản phẩm
                              const deleteResponse = await axios.delete(`http://localhost:8080/don-hang/${selectedInvoice.value.id}`);
                              if (deleteResponse.status === 200) {
                                    alert("✅ Hóa đơn đã được xóa thành công vì không có sản phẩm.");
                                    fetchInvoices(); // Tải lại danh sách hóa đơn sau khi xóa
                                    selectedInvoice.value = {}; // Reset hóa đơn đã chọn
                              } else {
                                    alert("❌ Không thể xóa hóa đơn.");
                              }
                        } catch (error) {
                              console.error("❌ Lỗi khi xóa hóa đơn:", error);
                              alert("⚠ Không thể xóa hóa đơn. Vui lòng thử lại!");
                        }
                  }
            };









            // Fetch product details (sanPhamCT)
            const fetchFilterData = async () => {
                  try {
                        const [danhMucRes, thuongHieuRes, chatLieuRes, deGiayRes] = await Promise.all([
                              axios.get("http://localhost:8080/danh-muc"),
                              axios.get("http://localhost:8080/thuong-hieu"),
                              axios.get("http://localhost:8080/chat-lieu"),
                              axios.get("http://localhost:8080/de-giay")
                        ]);

                        danhMucList.value = danhMucRes.data || [];
                        thuongHieuList.value = thuongHieuRes.data || [];
                        chatLieuList.value = chatLieuRes.data || [];
                        deGiayList.value = deGiayRes.data || [];
                  } catch (error) {
                        console.error("Lỗi khi tải dữ liệu bộ lọc: ", error);
                  }
            };

            const fetchSanPhamChiTiet = async () => {
                  loading.value = true;
                  try {
                        const response = await axios.get("http://localhost:8080/san-pham-chi-tiet", {
                              params: {
                                    keyword: searchQuerySPCT.value.trim(),
                                    tenDanhMuc: selectedDanhMuc.value,
                                    tenThuongHieu: selectedThuongHieu.value,
                                    tenChatLieu: selectedChatLieu.value,
                                    tenDeGiay: selectedDeGiay.value,
                              },
                        });
                        sanPhamCTList.value = response.data || [];
                  } catch (error) {
                        console.error("Lỗi khi tải sản phẩm: ", error);
                  } finally {
                        loading.value = false;
                  }
            };
            // 🔍 **Lọc danh sách sản phẩm theo từ khóa tìm kiếm**
            const filteredSanPhamCTList = computed(() => {
                  if (!searchQuerySPCT.value) return sanPhamCTList.value;
                  return sanPhamCTList.value.filter(spct =>
                        spct.tenSanPham.toLowerCase().includes(searchQuerySPCT.value.toLowerCase()) ||
                        (spct.tenThuongHieu && spct.tenThuongHieu.toLowerCase().includes(searchQuerySPCT.value.toLowerCase())) ||
                        (spct.tenDanhMuc && spct.tenDanhMuc.toLowerCase().includes(searchQuerySPCT.value.toLowerCase())) ||
                        (spct.tenChatLieu && spct.tenChatLieu.toLowerCase().includes(searchQuerySPCT.value.toLowerCase()))
                  );
            });




            // Fetch customers data

            // Hàm tìm kiếm khách hàng
            const searchCustomers = async (query) => {
                  if (query.trim() === '') {
                        filteredKhachHangList.value = [];
                        return;
                  }

                  loading.value = true;
                  try {
                        const response = await axios.get('http://localhost:8080/khach-hang', {
                              params: { searchQuery: query.trim() }
                        });

                        khachHangList.value = response.data || [];
                        filteredKhachHangList.value = khachHangList.value.filter((khachHang) =>
                              khachHang.hoTen.toLowerCase().includes(query.toLowerCase())
                        );
                  } catch (error) {
                        console.error('Lỗi khi tìm kiếm khách hàng:', error);
                  } finally {
                        loading.value = false;
                  }
            };

            const customLabel = (option) => {
                  return `${option.hoTen} - ${option.soDienThoai}`;
            };



            const fetchKhachHang = async () => {
                  loading.value = true;
                  errorMessage.value = "";
                  try {
                        const response = await axios.get("http://localhost:8080/khach-hang");
                        khachHangList.value = Array.isArray(response.data) ? response.data : []; // Ensure it's always an array
                  } catch (error) {
                        errorMessage.value = "Lỗi khi tải dữ liệu khách hàng. Vui lòng thử lại!";
                  } finally {
                        loading.value = false;
                  }
            };

            const selectCustomer = async (khachHang) => {
                  if (khachHang) {
                        // Cập nhật thông tin khách hàng vào hóa đơn
                        selectedInvoice.value.khachHang = khachHang; // Cập nhật khách hàng vào hóa đơn

                        // Cập nhật lại thông tin của hóa đơn từ API
                        try {
                              await axios.put(`http://localhost:8080/don-hang/${selectedInvoice.value.id}`, {
                                    trangThaiDonHang: selectedInvoice.value.trangThaiDonHang,
                                    idKhachHang: khachHang.id,  // Cập nhật id khách hàng
                                    tongTien: selectedInvoice.value.tongTien  // Giữ nguyên tổng tiền
                              });

                              alert(`Đã cập nhật khách hàng ${khachHang.hoTen} cho hóa đơn ${selectedInvoice.value.maDonHang}`);
                        } catch (error) {
                              console.error("Lỗi khi cập nhật khách hàng cho hóa đơn:", error);
                              alert("Không thể cập nhật khách hàng cho hóa đơn.");
                        }

                        // Đặt tên khách hàng vào ô tìm kiếm
                        searchQueryKH.value = khachHang.hoTen;
                  }
            };

            const filteredKhachHangList = computed(() => {
                  return khachHangList.value.filter(khachHang =>
                        khachHang.trangThai === true &&  // Kiểm tra trạng thái khách hàng là hoạt động
                        (khachHang.hoTen.toLowerCase().includes(searchQueryKH.value.toLowerCase()) ||
                              khachHang.soDienThoai.includes(searchQueryKH.value))
                  );
            });


            const paginatedSanPhamCTList = computed(() => {
                  const startIndex = (currentPageSPCT.value - 1) * pageSizeSPCT.value;
                  return filteredSanPhamCTList.value.slice(startIndex, startIndex + pageSizeSPCT.value);
            });
            const totalPagesSPCT = computed(() => Math.ceil(filteredSanPhamCTList.value.length / pageSizeSPCT.value));

            // Total number of pages
            const totalPages = computed(() => {
                  return Math.ceil(sanPhamCTList.value.length / pageSizeSPCT.value);
            });

            const visiblePages = computed(() => {
                  const maxPagesToShow = 5;
                  const total = totalPages.value;
                  const current = currentPage.value;

                  let start = Math.max(current - 2, 1);
                  let end = Math.min(current + 2, total);

                  if (start === 1) {
                        end = Math.min(start + maxPagesToShow - 1, total);
                  } else if (end === total) {
                        start = Math.max(total - maxPagesToShow + 1, 1);
                  }

                  return Array.from({ length: end - start + 1 }, (_, i) => start + i);
            });

            const handleAddKhachHang = () => {
                  router.push('/admin/customers/manage/add-khachhang');
            };
            // Pagination controls
            const nextPage = () => {
                  if (currentPageSPCT.value < totalPages.value) {
                        currentPageSPCT.value++;
                  }
            };

            const prevPage = () => {
                  if (currentPageSPCT.value > 1) {
                        currentPage.value--;
                  }
            };

            const changePage = (page) => {
                  currentPageSPCT.value = page;
            };

            // Handling product and customer view/edit
            const handleViewSanPhamChiTiet = (id) => {
                  router.push(`/admin/products/details/view-spct/${id}`);
            };

            const handleEditSanPhamChiTiet = (id) => {
                  router.push(`/admin/products/details/update-spct/${id}`);
            };
            const calculateDiscountedPrice = (spct) => {
                  console.log("Khuyến mãi trạng thái:", spct.khuyenMai ? spct.khuyenMai.trangThai : 'Không có khuyến mãi');
                  // Kiểm tra trạng thái khuyến mãi
                  console.log("Phần trăm giảm giá:", spct.phanTramGiamGia); // Kiểm tra phần trăm giảm giá

                  if (spct.phanTramGiamGia && spct.phanTramGiamGia > 0 && spct.trangThai === true) {
                        const discountAmount = (spct.giaBan * spct.phanTramGiamGia) / 100;
                        return spct.giaBan - discountAmount;
                  }

                  return spct.giaBan; // Trả về giá gốc nếu không có giảm giá hoặc trạng thái khuyến mãi là false
            };




            watch(selectedInvoice, (newVal) => {
                  console.log("Hóa đơn đã chọn thay đổi:", newVal);
            });


            // Fetch data on mounted
            onMounted(() => {
                  fetchFilterData();
                  fetchSanPhamChiTiet();
                  fetchInvoices();
                  fetchKhachHang();
                  fetchGioHang();
                  fetchOrderDetails();
                  fetchVouchers();
            });

            return {
                  router,
                  discountAmount,
                  userLogin,
                  totalAmountBeforeDiscount,
                  totalAmountAfterDiscount,
                  selectVoucher,
                  updateCustomerInInvoice,
                  loading,
                  errorMessage,
                  searchQuery,
                  orderDetails,
                  voucherList,
                  selectedVoucher,
                  fetchVouchers,
                  closeQRCode,
                  //hoa don
                  calculateDiscountedPrice,
                  filteredInvoices,
                  savedAmount,
                  applyDiscount,
                  selectOrderDetail,
                  removeFromOrder,
                  updateQuantityInOrder,
                  addProductToInvoice,
                  addToCart,

                  invoices,
                  fetchInvoices,
                  paginatedInvoices,
                  totalPagesInvoice,
                  prevPageInvoice,
                  nextPageInvoice,
                  changePageInvoice,
                  currentPageInvoice,
                  pageSizeInvoice,
                  visiblePagesInvoice,
                  createInvoice,
                  cancelInvoice,
                  selectedInvoice,
                  selectedInvoiceCT,

                  selectInvoice,
                  processPayment,
                  //san pham
                  fetchProductStock,
                  getDiscountFromSPCT,
                  paymentMethod,
                  qrGenerated,
                  showImage,
                  showAmountInput,
                  amountPaid,
                  payInvoice,
                  discountCode,
                  fetchOrderDetails,
                  sanPhamCTList,
                  searchQuerySPCT,
                  khachHangList,
                  paginatedSanPhamCTList,
                  selectedDanhMuc,
                  selectedThuongHieu,
                  selectedChatLieu,
                  selectedDeGiay,
                  danhMucList,
                  thuongHieuList,
                  chatLieuList,
                  deGiayList,
                  totalPages,
                  visiblePages,
                  currentPageSPCT,
                  pageSizeSPCT,
                  handleEditSanPhamChiTiet,
                  handleViewSanPhamChiTiet,
                  fetchSanPhamChiTiet,
                  paginatedSanPhamCTList,
                  totalPagesSPCT,
                  amountPaid,
                  customLabel,
                  isAmountInvalid,
                  calculateTotalAmount,
                  //Khach hang
                  filteredKhachHangList,
                  selectCustomer,
                  handleAddKhachHang,
                  searchQueryKH,
                  selectedKhachHang,
                  currentPageKH,
                  pageSizeKH,
                  currentPage,
                  pageSize,
                  nextPage,
                  prevPage,
                  changePage,
                  fetchKhachHang,
                  selectedInvoice,
                  selectedTrangThai,
                  cartItems,
                  removeFromCart,
                  updateQuantity,
                  totalAmount,

                  exportInvoice,
            };

      },
};
</script>

<style scoped>
.custom-table td.price-goc {
      text-decoration: line-through;
      /* Gạch ngang cho giá gốc */

}

.custom-table td.price-khuyen-mai {
      color: red;
      /* Màu đỏ cho giá khuyến mãi */
}

.custom-table td.price-thanh-tien {
      color: green;
      /* Màu xanh cho thành tiền */
}

/* Style for form inputs */
.form-control {
      width: 100%;
      padding: 0.75rem 1rem;
      margin-bottom: 12px;
}

/* Hover effect for table rows */
table tbody tr:hover {
      background-color: #f4f4f4;
}

/* Hover effect for buttons */
.btn:hover {
      transform: scale(1.05);
}

/* Margin between form groups */
.input-group {
      margin-bottom: 12px;
}

.bg-white {
      margin-bottom: 20px;
      padding: 20px;
}

.flex {
      display: flex;
      align-items: center;
}

.items-center {
      align-items: center;
}

.space-x-2 {
      margin-left: 8px;
}

.btn-success1 {
      background-color: #28a745;
      /* Màu xanh lá */
      color: white;
      /* Màu chữ trắng */
      border: none;
      padding: 8px 16px;
      border-radius: 4px;
      cursor: pointer;
      font-size: 16px;
      transition: background-color 0.3s;
}

.btn-success1:hover {
      background-color: #218838;
      /* Màu xanh đậm khi hover */
}

.btn-success {
      font-size: 1.25rem;
      /* Kích thước chữ lớn hơn */
      padding: 12px 24px;
      /* Tăng padding để nút rộng và cao hơn */
      height: auto;
      /* Đảm bảo chiều cao tự động điều chỉnh theo nội dung */
}

/* Pagination styles */
.pagination-container {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
}

.pagination-pages {
      display: flex;
      gap: 5px;
}

.pagination-btn {
      width: 40px;
      height: 40px;
      border: none;
      border-radius: 8px;
      background-color: #f0f0f0;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: background-color 0.2s ease;
}

.pagination-btn:hover {
      background-color: #dcdcdc;
}

.pagination-btn:disabled {
      background-color: #e0e0e0;
      cursor: not-allowed;
}

.pagination-page {
      width: 40px;
      height: 40px;
      border: none;
      border-radius: 8px;
      background-color: #f8f9fa;
      color: #007bff;
      font-weight: bold;
      cursor: pointer;
      transition: background-color 0.2s ease;
}

.pagination-page:hover {
      background-color: #e0e0e0;
}

.pagination-page.active {
      background-color: #007bff;
      color: white;
}

.pagination-select {
      width: 100px;
      height: 40px;
      border-radius: 8px;
      padding: 5px;
      border: 1px solid #ccc;
      cursor: pointer;
}




/* Giảm kích thước của các bảng */
table {
      font-size: 14px;
      /* Font chữ nhỏ hơn */
      width: 100%;
      /* Đảm bảo bảng chiếm hết chiều rộng của container */
      table-layout: fixed;
      /* Cải thiện hiển thị của bảng khi có nhiều dữ liệu */
}

/* Giảm padding của các ô trong bảng */
table th,
table td {
      padding: 8px 10px;
      /* Giảm khoảng cách trong các ô */
      text-align: center;
      /* Canh giữa các dữ liệu trong ô */
}

/* Giảm khoảng cách giữa các hàng */
table tbody tr {
      height: 35px;
      /* Đảm bảo hàng không chiếm quá nhiều chiều cao */
}

/* Cải thiện hiển thị các dòng */
table tbody tr:hover {
      background-color: #f4f4f4;
      /* Đảm bảo hàng được đánh dấu khi hover */
}

/* Thiết lập các bảng có thể cuộn ngang khi dữ liệu quá nhiều */
.table-wrapper {
      overflow-x: auto;
}

/* Điều chỉnh các nút phân trang */
.pagination-container {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 5px;
      /* Giảm khoảng cách giữa các nút */
}

.pagination-btn,
.pagination-page {
      width: 30px;
      /* Nút nhỏ hơn */
      height: 30px;
      /* Nút nhỏ hơn */
      font-size: 12px;
      /* Giảm kích thước chữ trên các nút */
      border-radius: 5px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: background-color 0.2s ease;
}

/* Các nút khi hover */
.pagination-btn:hover,
.pagination-page:hover {
      background-color: #e0e0e0;
}

/* Tối ưu hóa kiểu cho input trong bảng */
input[type="number"] {
      width: 60px;
      /* Giới hạn chiều rộng của ô nhập số lượng */
      padding: 5px;
      font-size: 12px;
}

/* Giảm cỡ chữ trong bảng */
table th,
table td {
      font-size: 0.775rem;
      /* Đặt cỡ chữ nhỏ hơn */
}

/* Nếu bạn muốn giảm cỡ chữ cho những cột quan trọng khác, ví dụ như cho phần tổng tiền */
table td.total-column,
table th.total-column {
      font-size: 0.75rem;
      /* Cỡ chữ nhỏ cho cột tổng tiền */
}

tr {
      cursor: pointer;
}

/* Sửa kích thước của container chứa mã QR */
#qr-image-container {
      position: relative;
      width: 250px;
      /* Tăng kích thước chiều rộng của container */
      height: 250px;
      /* Tăng kích thước chiều cao của container */
      display: flex;
      justify-content: center;
      align-items: center;
}

/* Điều chỉnh kích thước hình ảnh QR */
#qr-image-container img {
      width: 400%;
      /* Điều chỉnh chiều rộng ảnh để không chiếm hết container */
      height: 200px;
      /* Đảm bảo hình ảnh có tỷ lệ phù hợp */
      padding-left: 200px;
}

/* Đặt nút quay lại ở vị trí dưới cùng của hình ảnh */
#closeQRCode {
      background-color: rgba(255, 0, 0, 0.7);
      /* Nền màu đỏ với độ mờ */
      color: white;
      padding: 10px;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      font-size: 14px;
      font-weight: bold;
      transition: background-color 0.3s;
}

#closeQRCode:hover {
      background-color: rgba(255, 0, 0, 1);
      /* Đổi màu khi hover */
}

/* Thêm các class CSS cho các phương thức thanh toán */
.payment-methods {
      display: flex;
      gap: 30px;
      /* Tăng khoảng cách giữa các lựa chọn */
      align-items: center;
}

.payment-option {
      display: flex;
      align-items: center;
}

.payment-option input[type="radio"] {
      margin-right: 10px;
      /* Cách đều với label */
      transform: scale(1.2);
      /* Tăng kích thước nút radio để dễ dàng chọn */
}

.payment-option label {
      font-size: 1rem;
      font-weight: 600;
      color: #333;
      /* Màu chữ đen để dễ nhìn */
      cursor: pointer;
      transition: color 0.3s;
}

.payment-option label:hover {
      color: #007bff;
      /* Màu chữ khi hover */
}

.payment-option input[type="radio"]:checked+label {
      color: #28a745;
      /* Màu chữ khi radio được chọn */
}

/* Đảm bảo các phần tử có khoảng cách giữa nhau */
.mb-4 {
      margin-bottom: 20px;
      /* Tăng margin dưới cho các phần tử */
}

/* Tạo khoảng cách giữa các nhóm form */
.mb-4+.mb-4 {
      margin-top: 30px;
      /* Tăng khoảng cách giữa các form groups */
}

/* Cải thiện giao diện cho label của các phần tử */
label {
      font-size: 1rem;
      font-weight: bold;
}

/* Grid layout cho hóa đơn */
/* Grid layout cho hóa đơn */
h3 {
      display: flex;
      justify-content: space-between;
      /* Đẩy nút sang bên phải */
      align-items: center;
      /* Căn chỉnh nội dung dọc giữa */
}

.btn-primary {
      background-color: #007bff;
      color: white;
      font-size: 0.7rem;
      padding: 10px 20px;
      border-radius: 5px;
      border: none;
      cursor: pointer;
      transition: background-color 0.3s ease, transform 0.3s ease;
}

.btn-primary:hover {
      background-color: #0056b3;
      /* Màu sắc thay đổi khi hover */
      transform: scale(1.05);
      /* Tăng kích thước khi hover */
}

.btn-primary:focus {
      outline: none;
}

/* Thiết kế thẻ hóa đơn */
.grid {
      display: grid;
      gap: 12px;

      grid-template-columns: repeat(5, 1fr);
      /* 5 cột trong mỗi hàng */
}


.invoice-card {
      background-color: #fff;

      border-radius: 8px;
      padding: 8px;
      /* Giảm padding để thẻ nhỏ lại */
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
      transition: transform 0.3s ease-in-out, box-shadow 0.3s ease;
      font-size: 0.75rem;
      /* Giảm kích thước font */
      position: relative;
      /* Để dễ dàng đặt nút hủy */
      overflow: hidden;
      /* Tránh nội dung tràn ra ngoài */
      display: flex;
      flex-direction: column;
      /* Đảm bảo các thành phần trong thẻ xếp dọc */
      justify-content: space-between;
      /* Giữ khoảng cách đều */
}

.invoice-card:hover {
      transform: scale(1.05);
      box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.invoice-card h5 {
      font-size: 0.575rem;
      font-weight: 500;
      color: #333;
      margin-bottom: 2px;

      white-space: nowrap;
      /* Đảm bảo mã hóa đơn không bị chia dòng */
      overflow: hidden;
      text-overflow: ellipsis;
      /* Truncates long invoice codes */
}

.invoice-card p {
      font-size: 0.55rem;
      color: #555;
      margin: 2px 0;
      white-space: nowrap;
      /* Ngăn việc tràn dòng */
      overflow: hidden;
      text-overflow: ellipsis;
}

/* Nút hủy hóa đơn (dấu X) */
.cancel-btn {
      position: absolute;
      top: 4px;
      right: 0.01rem;
      background: transparent;
      border: none;
      font-size: 0.05rem;
      color: #e74c3c;
      /* Màu đỏ cho nút hủy */
      cursor: pointer;
      transition: color 0.3s ease;
}

.cancel-btn:hover {
      color: #c0392b;
      /* Màu đỏ đậm khi hover */
}

.cancel-btn i {
      font-size: 0.75rem;
      /* Tăng kích thước dấu X */
}

/* Pagination */
.pagination-container {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      margin-top: 16px;
}

.pagination-pages {
      display: flex;
      gap: 5px;
}

.pagination-btn {
      width: 30px;
      height: 30px;
      border: none;
      border-radius: 8px;
      background-color: #f0f0f0;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: background-color 0.2s ease;
}

.pagination-btn:hover {
      background-color: #dcdcdc;
}

.pagination-btn:disabled {
      background-color: #e0e0e0;
      cursor: not-allowed;
}

.pagination-page {
      width: 30px;
      height: 30x;
      border: none;
      border-radius: 8px;
      background-color: #f8f9fa;
      color: #007bff;
      font-weight: bold;
      cursor: pointer;
      transition: background-color 0.2s ease;
}

.pagination-page:hover {
      background-color: #e0e0e0;
}

.pagination-page.active {
      background-color: #007bff;
      color: white;
}

.pagination-select {
      width: 100px;
      height: 35px;
      border-radius: 8px;
      padding: 5px;
      border: 1px solid #ccc;
      cursor: pointer;
}

/* Giỏ hàng nhỏ gọn */
.cart-items {
      display: flex;
      flex-direction: column;
      gap: 12px;
}

.cart-item {
      position: relative;
      /* Để định vị dấu X */
      display: flex;
      justify-content: space-between;
      /* Căn chỉnh các phần tử theo hàng ngang */
      align-items: center;
      /* Căn chỉnh các phần tử thẳng hàng theo chiều dọc */
      background-color: #f9f9f9;
      padding: 10px;
      border-radius: 8px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      width: 100%;
      transition: transform 0.3s ease, box-shadow 0.3s ease;
      font-size: 0.875rem;
      flex-wrap: wrap;
      /* Cho phép các phần tử cuộn nếu không đủ không gian */
}

/* Hover effect */
.cart-item:hover {
      transform: scale(1.02);
      box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
}

/* Nút "Xóa" với dấu "X" */
.btn-remove {
      position: absolute;
      top: 8px;
      right: 8px;
      background: transparent;
      border: none;
      font-size: 1.25rem;
      color: #e74c3c;
      cursor: pointer;
      transition: color 0.3s ease;
}

.btn-remove:hover {
      color: #c0392b;
}

/* Wrapper cho ảnh sản phẩm */
.item-image-wrapper {
      flex-shrink: 0;
}

.item-image {
      width: 50px;
      /* Giảm kích thước ảnh */
      height: 50px;
      object-fit: cover;
      border-radius: 8px;
}

/* Thông tin sản phẩm */
.item-info {
      display: flex;
      align-items: center;
      /* Căn chỉnh tất cả các phần tử trong thẻ giỏ hàng thẳng hàng */
      justify-content: space-between;
      /* Căn chỉnh các thành phần theo hàng ngang */
      gap: 10px;
      flex-grow: 1;
      flex-wrap: wrap;
      /* Cho phép cuộn nếu không đủ không gian */
}

/* Đảm bảo các chi tiết nằm gọn bên trái và phần giá nằm bên phải */
.item-details {
      display: flex;
      flex-direction: column;
      gap: 4px;
      flex-grow: 1;
}

.item-price,
.quantity-actions,
.item-total {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      /* Căn chỉnh các phần tử này về phía bên phải */
      gap: 4px;
      flex-shrink: 0;
}

.item-name {
      font-size: 0.775rem;
      font-weight: bold;
      color: #333;
}

.item-code {
      font-size: 0.75rem;
      color: #777;
      margin-top: 1px;
}

.item-price {
      display: flex;
      gap: 8px;
      margin-top: 1px;
}

.price-goc {
      margin-top: -15px;
      text-decoration: line-through;
      color: #888;
}

.price-khuyen-mai {
      margin-top: -15px;
      color: #e74c3c;
}

.quantity-actions {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-top: 4px;
}

.quantity-input {
      margin-top: -15px;
      width: 50px;
      padding: 6px;
      font-size: 0.875rem;
      border-radius: 5px;
      border: 1px solid #ccc;
}

.item-total {

      margin-right: 95px;
      font-size: 0.875rem;
      font-weight: bold;
      margin-top: 4px;
      color: #333;
}

.total-amount {
      font-size: 1.125rem;
      color: green;
}

/* Responsive: Khi màn hình nhỏ, sản phẩm sẽ hiển thị theo cột */
@media (max-width: 768px) {
      .cart-item {
            flex-direction: column;
            gap: 8px;
            width: 100%;
      }

      .item-info {
            flex-direction: column;
            align-items: flex-start;
      }

      .item-image {
            width: 50px;
            height: 50px;
      }
}

@media (max-width: 480px) {
      .cart-item {
            width: 100%;
      }
}
</style>